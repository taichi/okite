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
package ninja.siden.okite.compiler.internal;

import io.gige.util.ElementFilter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.annotation.Generated;
import javax.annotation.processing.Filer;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import ninja.siden.okite.Constants;
import ninja.siden.okite.MessageResolver;
import ninja.siden.okite.ValidationContext;
import ninja.siden.okite.Violation;
import ninja.siden.okite.annotation.AnnotateWith.AnnotationTarget;
import ninja.siden.okite.annotation.Validate;
import ninja.siden.okite.compiler.Env;
import ninja.siden.okite.compiler.RoundEnvironment;
import ninja.siden.okite.internal.BaseValidator;
import ninja.siden.okite.util.Streams;

/**
 * @author taichi
 */
public class ValidatorWriter {

	final ValidationInfo targetType;

	final List<ConstraintGroup> members = new ArrayList<>();

	public ValidatorWriter(ValidationInfo target) {
		this.targetType = target;
	}

	public static ValidatorWriter of(Env env, ValidationInfo vi) {
		Stream<Optional<ConstraintGroup>> fields = ElementFilter
				.fieldsIn(vi.target()).filter(env.repos.constraintFilter())
				.map(f -> VariableConstraintGroup.of(env, vi, f));

		Stream<Optional<ConstraintGroup>> methods = ElementFilter
				.methodsIn(vi.target)
				.filter(valueMethods().and(env.repos.constraintFilter()))
				.map(m -> VariableConstraintGroup.of(env, vi, m));

		ValidatorWriter info = new ValidatorWriter(vi);
		Streams.unwrap(Stream.concat(fields, methods)).forEach(
				info.members::add);

		ElementFilter.methodsIn(vi.target).filter(validateMethods(env))
				.map(ee -> MethodConstraintGroup.of(env, ee))
				.forEach(info.members::add);

		info.members.sort(ConstraintGroup.comparator(env));

		return info;
	}

	static Predicate<ExecutableElement> valueMethods() {
		// TODO need debug information?
		return e -> e.getParameters().size() < 1
				&& e.getReturnType().getKind().equals(TypeKind.VOID) == false;
	}

	static Predicate<ExecutableElement> validateMethods(Env env) {
		// TODO need debug information?
		return argsFilter().and(returnTypeFilter(env)).and(
				e -> e.getAnnotation(Validate.class) != null);
	}

	static Predicate<ExecutableElement> argsFilter() {
		return e -> {
			List<? extends VariableElement> params = e.getParameters();
			return params.size() == 1
					&& params.get(0).asType().toString()
							.equals(ValidationContext.class.getCanonicalName());
		};
	}

	static Predicate<ExecutableElement> returnTypeFilter(Env env) {
		return e -> {
			TypeMirror tm = e.getReturnType();
			if (tm.getKind().equals(TypeKind.VOID)) {
				return false;
			}
			String type = tm.toString();
			if (Violation[].class.getCanonicalName().equals(type)) {
				return true;
			}
			if (type.endsWith("<" + Violation.class.getCanonicalName() + ">") == false) {
				return false;
			}
			return Stream
					.of(Collection.class, Stream.class)
					.map(env.elemUtils::getTypeElement)
					.filter(Optional::isPresent)
					.map(Optional::get)
					.anyMatch(
							te -> {
								return env.typeUtils.isAssignable(
										env.typeUtils.erasure(tm), te.asType());
							});
		};
	}

	public void write(Env env, RoundEnvironment roundEnv) {
		TypeElement te = targetType.target();
		String pkg = env.elemUtils.getPackageName(te);
		String cls = te.getQualifiedName().toString();
		String validatorName = pkg + "." + targetType.name();
		String simpleName = env.classUtils.toSimpleName(validatorName);

		Filer filer = env.env.getFiler();
		try {
			JavaFileObject jfo = filer.createSourceFile(validatorName, te);
			try (PrintWriter pw = new PrintWriter(new BufferedWriter(
					jfo.openWriter()))) {
				pw.format("package %s;%n", pkg);
				pw.println();
				pw.println("import java.util.*;");
				pw.println("import ninja.siden.okite.*;");
				pw.println("import ninja.siden.okite.internal.*;");
				pw.println();

				printAnnotations(pw, AnnotationTarget.TYPE);
				pw.println();
				printGenerated(env, pw);
				pw.format("public class %s extends %s<%s> {%n", simpleName,
						env.classUtils.toSimpleName(BaseValidator.class), cls);

				printAnnotations(pw, AnnotationTarget.CONSTRUCTOR);
				pw.println();
				printConstructor(simpleName, pw);

				members.forEach(cg -> cg.emit(roundEnv, pw));

				pw.println("}// class");
			}
		} catch (IOException e) {
			env.env.getMessager().printMessage(Kind.ERROR, e.getMessage(),
					targetType.target());
		}
	}

	void printAnnotations(PrintWriter pw, AnnotationTarget target) {
		this.targetType.with.stream().filter(awi -> awi.target == target)
				.forEach(awi -> {
					pw.format("@%s(%s)", awi.annotation, awi.attributes);
				});
	}

	void printConstructor(String simpleName, PrintWriter pw) {
		pw.printf("public %s(", simpleName);
		printAnnotations(pw, AnnotationTarget.CONSTRUCTOR_PARAMETER);
		pw.printf("%s resolver) {%n", MessageResolver.class.getSimpleName());
		pw.println("super(resolver);");
		pw.println("}// constructor");
	}

	void printGenerated(Env env, PrintWriter pw) {
		pw.printf(
				"@%s(value = { \"%s\", \"%s\" }, date = \"%tFT%<tT.%<tL%<tz\")%n",
				Generated.class.getName(), Constants.NAME,
				env.options.version(), env.options.now());
	}
}
