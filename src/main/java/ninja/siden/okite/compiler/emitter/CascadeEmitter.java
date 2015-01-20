/*
 * Copyright 2015 SATO taichi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package ninja.siden.okite.compiler.emitter;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleTypeVisitor8;
import javax.lang.model.util.Types;

import ninja.siden.okite.compiler.AnnotationValues;
import ninja.siden.okite.compiler.Elements;
import ninja.siden.okite.compiler.Env;
import ninja.siden.okite.compiler.RoundEnvironment;
import ninja.siden.okite.compiler.internal.ValidationInfo;
import ninja.siden.okite.constraint.CascadeConstraint;

/**
 * @author taichi
 */
public class CascadeEmitter extends DefaultEmitter {

	// TODO type level scope cascading is not implemented.
	@Override
	public boolean emit(Env env, RoundEnvironment roundEnv, PrintWriter pw,
			AnnotationMirror am, Element element, TypeMirror type) {
		if (cascade(env, am) == false) {
			return false;
		}
		Optional<String> opt = findValidator(env, roundEnv, type);
		if (opt.isPresent()) {
			String cn = CascadeConstraint.class.getCanonicalName();
			String t = env.elemUtils.toBoxedClassName(type);
			pw.printf("%s<%s> c = new %s<>(new %s(resolver));%n", cn, t, cn,
					opt.get());
			return emitMembers(env, pw, am);
		}

		return decide(env, type)
				.map(constType -> {
					String cn = constType[0];
					String t = env.elemUtils.toBoxedClassName(type);
					String inner = constType[1];
					return env.elemUtils
							.getTypeElement(inner)
							.flatMap(
									te -> findValidator(env, roundEnv,
											te.asType()))
							.map(valdr -> {
								pw.printf(
										"%s<%s, %s> c = new %s<>(new %s(resolver));%n",
										cn, inner, t, cn, valdr);
								return emitMembers(env, pw, am);
							}).orElse(false);
				}).orElse(false);
	}

	protected boolean cascade(Env env, AnnotationMirror am) {
		return env.values.get(am).anyMatch(av -> {
			return AnnotationValues.readBoolean(av).orElse(false);
		});
	}

	protected Optional<String> findValidator(Env env,
			RoundEnvironment roundEnv, TypeMirror type) {
		Optional<String> opt = roundEnv
				.getTargets()
				.filter(vi -> env.typeUtils.isSameType(type, vi.target()
						.asType())).map(ValidationInfo::name).findFirst();
		if (opt.isPresent()) {
			return opt;
		}

		Optional<TypeElement> optTE = env.elemUtils.toTypeElement(type);
		return optTE.flatMap(target -> {
			Stream<ValidationInfo> vals = findValidation(env,
					env.repos.validations(), target,
					am -> ValidationInfo.from(env, target, am));
			Stream<ValidationInfo> metaVals = findValidation(env,
					env.repos.metaValidations(), target,
					am -> ValidationInfo.fromMeta(env, target, am));
			return Stream.concat(vals, metaVals).map(ValidationInfo::name)
					.findFirst();
		});
	}

	protected Stream<ValidationInfo> findValidation(Env env,
			Stream<TypeElement> anons, TypeElement target,
			Function<AnnotationMirror, ValidationInfo> fn) {
		return anons.flatMap(a -> env.elemUtils.findAnnotation(target, a).map(
				am -> fn.apply(am)));
	}

	protected Optional<String[]> decide(Env env, TypeMirror target) {
		Types typeUtils = env.typeUtils;
		Elements elemUtils = env.elemUtils;

		if (target.getKind() == TypeKind.ARRAY) {
			String[] s = { CascadeConstraint.ForArray.class.getCanonicalName(),
					toComponentType(env, target) };
			return Optional.of(s);
		}

		TypeMirror erasure = typeUtils.erasure(target);
		if (typeUtils.isAssignable(erasure,
				elemUtils.getTypeElement(Iterable.class).get().asType())) {
			String[] s = {
					CascadeConstraint.ForIterable.class.getCanonicalName(),
					toVariableType(env, target, 0) };
			return Optional.of(s);
		}
		if (typeUtils.isAssignable(erasure, elemUtils.getTypeElement(Map.class)
				.get().asType())) {
			String[] s = { CascadeConstraint.ForMap.class.getCanonicalName(),
					toVariableType(env, target, 1) };
			return Optional.of(s);
		}
		return Optional.empty();
	}

	protected String toVariableType(Env env, TypeMirror target, int index) {
		return target.accept(
				new SimpleTypeVisitor8<StringBuilder, StringBuilder>() {
					@Override
					public StringBuilder visitDeclared(DeclaredType t,
							StringBuilder p) {
						List<? extends TypeMirror> vars = t.getTypeArguments();
						if (index < vars.size()) {
							vars.get(index).accept(
									env.elemUtils.newQNVisitor(), p);
						}
						return p;
					}
				}, new StringBuilder()).toString();
	}

	protected String toComponentType(Env env, TypeMirror target) {
		return target.accept(
				new SimpleTypeVisitor8<StringBuilder, StringBuilder>() {
					@Override
					public StringBuilder visitArray(ArrayType t, StringBuilder p) {
						t.getComponentType().accept(
								env.elemUtils.newQNVisitor(), p);
						return p;
					}
				}, new StringBuilder()).toString();
	}
}
