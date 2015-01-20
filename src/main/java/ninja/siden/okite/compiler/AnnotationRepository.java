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
import java.lang.annotation.Repeatable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import ninja.siden.okite.annotation.Cascade;
import ninja.siden.okite.annotation.Emitter;
import ninja.siden.okite.annotation.Implements;
import ninja.siden.okite.compiler.emitter.ImplementsEmitter;
import ninja.siden.okite.compiler.internal.CascadeHolder;
import ninja.siden.okite.compiler.internal.ConstraintInfo;
import ninja.siden.okite.util.Streams;

/**
 * @author taichi
 */
public class AnnotationRepository {

	final Env env;
	final List<TypeElement> validations = new ArrayList<>();
	final List<TypeElement> metaValidations = new ArrayList<>();
	final Set<TypeMirror> repeatables = new HashSet<>();
	final Set<TypeMirror> constraints = new HashSet<>();
	final Map<String, ConstraintEmitter> entries = new HashMap<>();
	final AnnotationMirror cascade;

	public AnnotationRepository(Env env) {
		this.env = env;

		Streams.unwrap(
				this.env.builtIns.constraint().map(
						clazz -> this.env.elemUtils.getTypeElement(clazz)))
				.forEach(te -> addConstraint(te.asType()));

		Streams.unwrap(
				env.builtIns.validation().map(
						clazz -> this.env.elemUtils.getTypeElement(clazz)))
				.forEach(validations::add);

		this.cascade = env.elemUtils
				.getTypeElement(CascadeHolder.class)
				.flatMap(
						te -> ElementFilter.fieldsIn(te.getEnclosedElements())
								.findFirst())
				.flatMap(
						elem -> env.elemUtils.findAnnotation(elem,
								Cascade.class).findFirst()).get();
	}

	public void add(RoundEnvironment roundEnv) {
		metaAnnotations(roundEnv, this.env.builtIns.metaConstraint()).forEach(
				te -> addConstraint(te.asType()));

		metaAnnotations(roundEnv, this.env.builtIns.validation()).map(
				TypeElement.class::cast).forEach(metaValidations::add);
	}

	protected Stream<TypeElement> metaAnnotations(RoundEnvironment roundEnv,
			Stream<Class<? extends Annotation>> meta) {
		return meta.flatMap(roundEnv::getElementsAnnotatedWith)
				.filter(ElementFilter.of(ElementKind.ANNOTATION_TYPE))
				.map(TypeElement.class::cast);
	}

	protected void addConstraint(TypeMirror type) {
		String name = this.env.elemUtils.getClassName(type);
		if (this.entries.containsKey(name)) {
			return;
		}

		Optional<TypeElement> opt = env.elemUtils.toTypeElement(type);
		opt.flatMap(
				te -> {
					Optional<ConstraintEmitter> impOpt = env.elemUtils
							.readAnnotation(te, Implements.class)
							.map(AnnotationValues::readType)
							.filter(Optional::isPresent).map(Optional::get)
							.findAny().map(tm -> new ImplementsEmitter(tm));
					if (impOpt.isPresent()) {
						return impOpt;
					}

					Optional<String> emOpt = env.elemUtils
							.readAnnotation(te, Emitter.class)
							.map(AnnotationValues::readString)
							.filter(Optional::isPresent).map(Optional::get)
							.findAny();
					return emOpt.flatMap(env.classUtils::newInstance);
				}).ifPresent(ce -> {
			entries.put(name, ce);
			constraints.add(type);
			checkRepeatable(type);
		});
	}

	void checkRepeatable(TypeMirror type) {
		env.elemUtils.findAnnotation(type, Repeatable.class)
				.flatMap(am -> env.values.get(am))
				.map(AnnotationValues::readType).filter(Optional::isPresent)
				.map(Optional::get).forEach(this.repeatables::add);
	}

	public boolean isRepeatable(TypeMirror other) {
		return this.repeatables.stream().anyMatch(
				my -> env.typeUtils.isSameType(my, other));
	}

	public Stream<ConstraintInfo> find(AnnotationMirror am) {
		TypeMirror key = am.getAnnotationType();
		Optional<ConstraintEmitter> opt = find(key);
		if (opt.isPresent()) {
			return Stream.of(new ConstraintInfo(env, am, opt.get()));
		}
		if (isRepeatable(key)) {
			return Streams.unwrap(this.env.values
					.get(am)
					.flatMap(av -> AnnotationValues.toAnnotations(av).stream())
					.map(am2 -> find(am2.getAnnotationType()).map(
							ce2 -> new ConstraintInfo(env, am2, ce2))));
		}
		return Stream.empty();
	}

	protected Optional<ConstraintEmitter> find(TypeMirror key) {
		String name = this.env.elemUtils.getClassName(key);
		ConstraintEmitter ce = this.entries.get(name);
		return Optional.ofNullable(ce);
	}

	public AnnotationMirror dummyCascade() {
		return this.cascade;
	}

	public Stream<TypeElement> validations() {
		return this.validations.stream();
	}

	public Stream<TypeElement> metaValidations() {
		return this.metaValidations.stream();
	}

	public Stream<TypeElement> constraints() {
		return this.constraints.stream()
				.map(tm -> this.env.typeUtils.asElement(tm))
				.filter(ElementFilter.of(ElementKind.ANNOTATION_TYPE))
				.map(TypeElement.class::cast);
	}

	public <T extends Element> Predicate<T> constraintFilter() {
		return e -> {
			List<? extends AnnotationMirror> mirrors = e.getAnnotationMirrors();
			return mirrors.stream().anyMatch(am -> {
				return constraints.stream().anyMatch(tm -> {
					TypeMirror annon = am.getAnnotationType();
					return this.env.typeUtils.isSameType(annon, tm);
				});
			});
		};
	}
}
