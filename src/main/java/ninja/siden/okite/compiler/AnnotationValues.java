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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleAnnotationValueVisitor8;

/**
 * @author taichi
 */
public class AnnotationValues {

	final ProcessingEnvironment env;

	public AnnotationValues(ProcessingEnvironment env) {
		this.env = env;
	}

	public Stream<? extends AnnotationValue> get(AnnotationMirror am) {
		return get(am, "value");
	}

	public Stream<? extends AnnotationValue> get(AnnotationMirror am,
			CharSequence key) {
		return env.getElementUtils().getElementValuesWithDefaults(am)
				.entrySet().stream()
				.filter(e -> e.getKey().getSimpleName().contentEquals(key))
				.map(Map.Entry::getValue);
	}

	public static Optional<TypeMirror> readType(AnnotationValue av) {
		TypeMirror value = av.accept(
				new SimpleAnnotationValueVisitor8<TypeMirror, Void>() {
					@Override
					public TypeMirror visitType(TypeMirror t, Void p) {
						return t;
					}
				}, null);
		return Optional.ofNullable(value);
	}

	public static Optional<String> readString(AnnotationValue av) {
		String value = av.accept(
				new SimpleAnnotationValueVisitor8<String, Void>() {
					@Override
					public String visitString(String s, Void p) {
						return s;
					}
				}, null);
		return Optional.ofNullable(value);
	}

	public static Optional<Integer> readInteger(AnnotationValue av) {
		Integer value = av.accept(
				new SimpleAnnotationValueVisitor8<Integer, Void>() {
					@Override
					public Integer visitInt(int i, Void p) {
						return i;
					}
				}, null);
		return Optional.ofNullable(value);
	}

	public static Optional<Boolean> readBoolean(AnnotationValue av) {
		Boolean value = av.accept(
				new SimpleAnnotationValueVisitor8<Boolean, Void>() {
					@Override
					public Boolean visitBoolean(boolean b, Void p) {
						return b;
					}
				}, null);
		return Optional.ofNullable(value);
	}

	public static Optional<VariableElement> readEnum(AnnotationValue av) {
		VariableElement value = av.accept(
				new SimpleAnnotationValueVisitor8<VariableElement, Void>() {
					@Override
					public VariableElement visitEnumConstant(VariableElement c,
							Void p) {
						return c;
					}
				}, null);
		return Optional.ofNullable(value);
	}

	public static List<AnnotationMirror> toAnnotations(AnnotationValue value) {
		return value
				.accept(new SimpleAnnotationValueVisitor8<List<AnnotationMirror>, List<AnnotationMirror>>() {
					@Override
					public List<AnnotationMirror> visitArray(
							List<? extends AnnotationValue> values,
							List<AnnotationMirror> p) {
						values.forEach(av -> av.accept(this, p));
						return p;
					}

					@Override
					public List<AnnotationMirror> visitAnnotation(
							AnnotationMirror a, List<AnnotationMirror> p) {
						p.add(a);
						return p;
					}
				}, new ArrayList<>());
	}

}
