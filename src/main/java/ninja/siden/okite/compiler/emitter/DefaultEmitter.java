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
import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;

import ninja.siden.okite.compiler.ConstraintEmitter;
import ninja.siden.okite.compiler.Env;
import ninja.siden.okite.compiler.internal.ToStringVisitor;

/**
 * @author taichi
 */
public abstract class DefaultEmitter implements ConstraintEmitter {

	protected boolean emitMembers(Env env, PrintWriter pw, AnnotationMirror am) {
		Map<? extends ExecutableElement, ? extends AnnotationValue> values = env.env
				.getElementUtils().getElementValuesWithDefaults(am);
		values.entrySet()
				.stream()
				.forEach(
						entry -> {
							ExecutableElement ee = entry.getKey();
							StringBuilder v = entry.getValue().accept(
									new ToStringVisitor(ee, env),
									new StringBuilder());
							pw.printf("c.%s(%s);%n", ee.getSimpleName(), v);
						});
		return true;
	}
}
