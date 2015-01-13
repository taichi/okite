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

import java.lang.annotation.Annotation;
import java.util.stream.Stream;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * @author taichi
 */
public class RoundEnvironment {

	final javax.annotation.processing.RoundEnvironment env;

	public RoundEnvironment(javax.annotation.processing.RoundEnvironment env) {
		this.env = env;
	}

	public Stream<? extends Element> getRootElements() {
		return this.env.getRootElements().stream();
	}

	public Stream<? extends Element> getElementsAnnotatedWith(TypeElement a) {
		return this.env.getElementsAnnotatedWith(a).stream();
	}

	public Stream<? extends Element> getElementsAnnotatedWith(
			Class<? extends Annotation> a) {
		return this.env.getElementsAnnotatedWith(a).stream();
	}
}
