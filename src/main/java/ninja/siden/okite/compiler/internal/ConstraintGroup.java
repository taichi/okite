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
import java.util.Comparator;

import javax.lang.model.element.Element;
import javax.lang.model.element.Name;

import ninja.siden.okite.annotation.Order;
import ninja.siden.okite.compiler.AnnotationValues;
import ninja.siden.okite.compiler.Env;
import ninja.siden.okite.compiler.RoundEnvironment;

/**
 * @author taichi
 */
public abstract class ConstraintGroup {

	Env env;

	int order;

	Element member; // method or field

	public ConstraintGroup(Env env, Element member) {
		this.env = env;
		this.member = member;
		this.order = env.elemUtils.findAnnotation(member, Order.class)
				.flatMap(am -> env.values.get(am)).findFirst()
				.flatMap(AnnotationValues::readInteger).orElse(0);
	}

	public String name() {
		return member.getSimpleName().toString();
	}

	public void emit(RoundEnvironment roundEnv, PrintWriter pw) {
		Name sn = member.getSimpleName();
		pw.printf("{ // BEGIN %s order:%d %n", sn, order);
		emitBody(roundEnv, pw);
		pw.printf("} // END   %s %n", sn);
	}

	public static Comparator<ConstraintGroup> comparator(Env env) {
		return (l, r) -> {
			int i = Integer.compare(l.order, r.order);
			return i == 0 ? env.options.memberComparator().compare(l.member,
					r.member) : i;
		};
	}

	protected abstract void emitBody(RoundEnvironment roundEnv, PrintWriter pw);
}
