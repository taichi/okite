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

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;

import ninja.siden.okite.annotation.AnnotateWith.AnnotationTarget;
import ninja.siden.okite.compiler.AnnotationValues;
import ninja.siden.okite.compiler.Env;

/**
 * @author taichi
 */
public class AnnotateWithInfo {

	String annotation;

	AnnotationTarget target = AnnotationTarget.CONSTRUCTOR;

	String attributes = "";

	public static Optional<AnnotateWithInfo> of(Env env, Element target,
			AnnotationMirror with) {
		AnnotateWithInfo info = new AnnotateWithInfo();
		Map<? extends ExecutableElement, ? extends AnnotationValue> values = with
				.getElementValues();
		for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> e : values
				.entrySet()) {
			Name key = e.getKey().getSimpleName();
			if (key.contentEquals("value")) {
				Optional<TypeMirror> te = AnnotationValues.readType(e
						.getValue());
				info.annotation = te.map(tm -> env.elemUtils.getClassName(tm))
						.orElse("");
			}
			if (key.contentEquals("target")) {
				Optional<VariableElement> opt = AnnotationValues.readEnum(e
						.getValue());
				info.target = opt.map(ve -> {
					String name = ve.getSimpleName().toString();
					return AnnotationTarget.valueOf(name);
				}).orElse(AnnotationTarget.CONSTRUCTOR);
			}
			if (key.contentEquals("values")) {
				Optional<String> opt = AnnotationValues
						.readString(e.getValue());
				info.attributes = opt.orElse("");
			}
		}
		Stream<TypeElement> vals = Stream.concat(env.repos.validations(),
				env.repos.metaValidations());
		Optional<TypeElement> tm = env.elemUtils
				.getTypeElement(info.annotation);

		if (tm.isPresent()
				&& vals.noneMatch(te -> env.typeUtils.isSameType(tm.get()
						.asType(), te.asType()))) {
			return Optional.of(info);
		}
		// TODO collect error message.
		env.env.getMessager().printMessage(Kind.ERROR,
				"You cannot apply @" + info.annotation, target);
		return Optional.empty();
	}
}
