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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;

import ninja.siden.okite.annotation.Validation;
import ninja.siden.okite.compiler.AnnotationValues;
import ninja.siden.okite.compiler.Env;
import ninja.siden.okite.util.Streams;

/**
 * @author taichi
 */
public class ValidationInfo {

	String prefix;

	String suffix;

	boolean cascading;

	List<AnnotateWithInfo> with;

	final TypeElement target;

	public ValidationInfo(TypeElement target) {
		this.target = target;
	}

	public TypeElement target() {
		return this.target;
	}

	public String name() {
		return this.prefix + this.target.getSimpleName() + this.suffix;
	}

	public static ValidationInfo from(Env env, TypeElement target) {
		ValidationInfo info = new ValidationInfo(target);
		info.prefix = env.options.prefix();
		info.suffix = env.options.suffix();
		info.cascading = env.options.casecading();
		return info;
	}

	public static ValidationInfo from(Env env, TypeElement target,
			AnnotationMirror validation) {
		ValidationInfo info = from(env, target);
		fill(env, validation, target, info);
		return info;
	}

	static void fill(Env env, AnnotationMirror validation, TypeElement target,
			ValidationInfo info) {
		Map<? extends ExecutableElement, ? extends AnnotationValue> values = env.env
				.getElementUtils().getElementValuesWithDefaults(validation);
		for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> e : values
				.entrySet()) {
			Name key = e.getKey().getSimpleName();
			if (key.contentEquals("prefix")) {
				Optional<String> opt = AnnotationValues
						.readString(e.getValue());
				info.prefix = opt.orElse(env.options.prefix());
			}
			if (key.contentEquals("suffix")) {
				Optional<String> opt = AnnotationValues
						.readString(e.getValue());
				info.suffix = opt.orElse(env.options.suffix());
			}
			if (key.contentEquals("cascading")) {
				info.cascading = AnnotationValues.readBoolean(e.getValue())
						.orElse(env.options.casecading());
			}
			if (key.contentEquals("with")) {
				info.with = Streams
						.unwrap(AnnotationValues
								.toAnnotations(e.getValue())
								.stream()
								.map(am -> AnnotateWithInfo.of(env, target, am)))
						.collect(Collectors.toList());
			}
		}
	}

	public static ValidationInfo fromMeta(Env env, TypeElement target,
			AnnotationMirror metaValidation) {
		AnnotationMirror validation = env.elemUtils
				.findAnnotation(metaValidation.getAnnotationType(),
						Validation.class).findFirst().get();
		ValidationInfo info = from(env, target, validation);
		fill(env, metaValidation, target, info);
		return info;
	}

	@Override
	public String toString() {
		StringBuilder stb = new StringBuilder();
		stb.append("target[");
		stb.append(this.target.toString());
		stb.append("]");

		return stb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (cascading ? 1231 : 1237);
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		result = prime * result + ((suffix == null) ? 0 : suffix.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		result = prime * result + ((with == null) ? 0 : with.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ValidationInfo other = (ValidationInfo) obj;
		if (cascading != other.cascading)
			return false;
		if (prefix == null) {
			if (other.prefix != null)
				return false;
		} else if (!prefix.equals(other.prefix))
			return false;
		if (suffix == null) {
			if (other.suffix != null)
				return false;
		} else if (!suffix.equals(other.suffix))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		if (with == null) {
			if (other.with != null)
				return false;
		} else if (!with.equals(other.with))
			return false;
		return true;
	}
}
