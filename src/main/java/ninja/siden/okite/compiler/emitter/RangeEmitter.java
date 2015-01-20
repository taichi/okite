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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;

import ninja.siden.okite.compiler.Elements;
import ninja.siden.okite.compiler.Env;
import ninja.siden.okite.compiler.RoundEnvironment;
import ninja.siden.okite.constraint.RangeConstraint;

/**
 * @author taichi
 */
public class RangeEmitter extends DefaultEmitter {

	@Override
	public boolean emit(Env env, RoundEnvironment roundEnv, PrintWriter pw,
			AnnotationMirror am, Element element, TypeMirror type) {
		TypeMirror target = env.elemUtils.toBoxedType(type);
		Optional<String> constType = decide(env, target);
		return constType.map(
				s -> {
					pw.printf("%s<%s> c = new %s<>();%n", s,
							env.elemUtils.getClassName(target), s);
					return emitMembers(env, pw, am);
				}).orElseGet(() -> {
			// TODO collect error message
				env.env.getMessager().printMessage(
						Kind.ERROR,
						"@Range can apply to Number(including primitives) and CharSequence. "
								+ type.toString() + " is not applicable.",
						element);
				return false;
			});
	}

	protected Optional<String> decide(Env env, TypeMirror target) {
		Types typeUtils = env.typeUtils;
		Elements elemUtils = env.elemUtils;
		if (typeUtils
				.isAssignable(target, elemUtils.boxedType(TypeKind.DOUBLE))) {
			return Optional.of(RangeConstraint.ForDouble.class
					.getCanonicalName());
		}
		if (typeUtils.isAssignable(target, elemUtils.boxedType(TypeKind.FLOAT))) {
			return Optional.of(RangeConstraint.ForFloat.class
					.getCanonicalName());
		}
		if (typeUtils.isAssignable(target,
				elemUtils.getTypeElement(BigDecimal.class).get().asType())) {
			return Optional.of(RangeConstraint.ForBigDecimal.class
					.getCanonicalName());
		}
		if (typeUtils.isAssignable(target,
				elemUtils.getTypeElement(BigInteger.class).get().asType())) {
			return Optional.of(RangeConstraint.ForBigInteger.class
					.getCanonicalName());
		}
		if (typeUtils.isAssignable(target,
				elemUtils.getTypeElement(Number.class).get().asType())) {
			return Optional.of(RangeConstraint.ForNumber.class
					.getCanonicalName());
		}
		if (typeUtils.isAssignable(target,
				elemUtils.getTypeElement(CharSequence.class).get().asType())) {
			return Optional.of(RangeConstraint.ForCharSequence.class
					.getCanonicalName());
		}
		return Optional.empty();
	}
}
