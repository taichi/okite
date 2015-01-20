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
package ninja.siden.okite.compiler;

import io.gige.util.ElementFilter;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import ninja.siden.okite.compiler.internal.ValidationInfo;
import ninja.siden.okite.util.Streams;

/**
 * @author taichi
 */
public class RoundEnvironment {

	final javax.annotation.processing.RoundEnvironment roundEnv;
	final Set<ValidationInfo> targets;

	public RoundEnvironment(Env env,
			javax.annotation.processing.RoundEnvironment roundEnv) {
		this.roundEnv = roundEnv;
		env.repos.add(this);
		this.targets = findTargets(env);
	}

	public Stream<? extends Element> getRootElements() {
		return this.roundEnv.getRootElements().stream();
	}

	public Stream<? extends Element> getElementsAnnotatedWith(TypeElement a) {
		return this.roundEnv.getElementsAnnotatedWith(a).stream();
	}

	public Stream<? extends Element> getElementsAnnotatedWith(
			Class<? extends Annotation> a) {
		return this.roundEnv.getElementsAnnotatedWith(a).stream();
	}

	public Stream<ValidationInfo> getTargets() {
		return this.targets.stream();
	}

	Set<ValidationInfo> findTargets(Env env) {
		Set<ValidationInfo> validations = Streams.unwrap(
				Stream.concat(fromAnnotations(env), fromMetaAnnotations(env)))
				.collect(Collectors.toSet());

		// TODO purge this feature?
		addAnnotationLessTargets(env, validations);
		return validations;
	}

	Stream<Optional<ValidationInfo>> validationInfo(Env env, TypeElement a,
			BiFunction<TypeElement, AnnotationMirror, ValidationInfo> fn) {
		return getElementsAnnotatedWith(a)
				.filter(ElementFilter.<Element> of(ElementKind.CLASS))
				.map(TypeElement.class::cast)
				.map(target -> env.elemUtils.findAnnotation(target, a)
						.findFirst().map(am -> fn.apply(target, am)));
	}

	Stream<Optional<ValidationInfo>> fromAnnotations(Env env) {
		return env.repos.validations().flatMap(
				a -> validationInfo(env, a,
						(target, am) -> ValidationInfo.from(env, target, am)));
	}

	Stream<Optional<ValidationInfo>> fromMetaAnnotations(Env env) {
		return env.repos.metaValidations().flatMap(
				a -> validationInfo(env, a, (target, am) -> ValidationInfo
						.fromMeta(env, target, am)));
	}

	void addAnnotationLessTargets(Env env, Set<ValidationInfo> validations) {
		Set<TypeElement> targets = validations.stream()
				.map(ValidationInfo::target).collect(Collectors.toSet());

		Streams.unwrap(
				env.repos
						.constraints()
						.flatMap(this::getElementsAnnotatedWith)
						.map(Element::getEnclosingElement)
						.map(e -> {
							switch (e.getKind()) {
							case CLASS:
								return Optional.of(TypeElement.class.cast(e));
							case METHOD:
							case FIELD:
								return Optional.of(TypeElement.class.cast(e
										.getEnclosingElement()));
							default:
								return Optional.empty();
							}
						})).filter(te -> targets.contains(te) == false)
				.map(te -> ValidationInfo.from(env, te))
				.forEach(validations::add);
	}

}
