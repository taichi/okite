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

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleAnnotationValueVisitor8;

/**
 * @author taichi
 */
public interface AnnotationValues {

	static Stream<? extends AnnotationValue> get(AnnotationMirror am) {
		return get(am, "value");
	}

	static Stream<? extends AnnotationValue> get(AnnotationMirror am,
			CharSequence key) {
		return am.getElementValues().entrySet().stream()
				.filter(e -> e.getKey().getSimpleName().contentEquals(key))
				.map(Map.Entry::getValue);
	}

	static Optional<TypeMirror> readType(AnnotationValue av) {
		TypeMirror value = av.accept(
				new SimpleAnnotationValueVisitor8<TypeMirror, Void>() {
					@Override
					public TypeMirror visitType(TypeMirror t, Void p) {
						return t;
					}
				}, null);
		return Optional.ofNullable(value);
	}

	static Optional<String> readString(AnnotationValue av) {
		String value = av.accept(
				new SimpleAnnotationValueVisitor8<String, Void>() {
					@Override
					public String visitString(String s, Void p) {
						return s;
					}
				}, null);
		return Optional.ofNullable(value);
	}
}
