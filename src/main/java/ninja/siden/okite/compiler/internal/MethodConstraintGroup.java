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

import java.io.PrintWriter;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import ninja.siden.okite.annotation.Validate;
import ninja.siden.okite.compiler.Env;
import ninja.siden.okite.compiler.RoundEnvironment;
import ninja.siden.okite.compiler.emitter.ValidateEmitter;

/**
 * @author taichi
 */
public class MethodConstraintGroup extends ConstraintGroup {

	public MethodConstraintGroup(Env env, Element member) {
		super(env, member);
	}

	public static MethodConstraintGroup of(Env env, ExecutableElement ee) {
		return new MethodConstraintGroup(env, ee);
	}

	@Override
	protected void emitBody(RoundEnvironment roundEnv, PrintWriter pw) {
		ValidateEmitter emitter = new ValidateEmitter();
		this.env.elemUtils
				.findAnnotation(this.member, Validate.class)
				.findFirst()
				.map(am -> emitter.emit(env, roundEnv, pw, am, this.member,
						null));
	}
}
