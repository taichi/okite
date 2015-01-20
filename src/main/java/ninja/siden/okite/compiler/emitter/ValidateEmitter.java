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
import java.util.Optional;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.type.TypeMirror;

import ninja.siden.okite.Constraint;
import ninja.siden.okite.compiler.AnnotationValues;
import ninja.siden.okite.compiler.ConstraintEmitter;
import ninja.siden.okite.compiler.Env;
import ninja.siden.okite.compiler.RoundEnvironment;

/**
 * @author taichi
 */
public class ValidateEmitter implements ConstraintEmitter {

	@Override
	public boolean emit(Env env, RoundEnvironment roundEnv, PrintWriter pw,
			AnnotationMirror am, Element element, TypeMirror type) {
		Name sn = element.getSimpleName();
		Optional<? extends AnnotationValue> opt = env.values.get(am, "policy")
				.findFirst();
		String policy = opt
				.flatMap(AnnotationValues::readEnum)
				.map(ve -> {
					return Constraint.Policy.class.getCanonicalName() + "."
							+ ve.getSimpleName();
				})
				.orElse(Constraint.Policy.ContinueToNextTarget.getClass()
						.getCanonicalName());

		pw.printf(
				"validations.add((v, c) -> convert(%s, v.%s(newContext(c, \"%s\"))));",
				policy, sn, sn);

		return true;
	}

}
