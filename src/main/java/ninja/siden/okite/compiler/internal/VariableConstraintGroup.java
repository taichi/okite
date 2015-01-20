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
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import ninja.siden.okite.Constraint;
import ninja.siden.okite.compiler.Env;
import ninja.siden.okite.compiler.RoundEnvironment;
import ninja.siden.okite.compiler.emitter.CascadeEmitter;

/**
 * @author taichi
 */
public class VariableConstraintGroup extends ConstraintGroup {

	TypeMirror type;

	List<ConstraintInfo> constraints;

	public VariableConstraintGroup(Env env, Element member, TypeMirror type) {
		super(env, member);
		this.type = type;
	}

	public static Optional<ConstraintGroup> of(Env env, ValidationInfo vi,
			ExecutableElement method) {
		return of(env, vi, method, method.getReturnType());
	}

	public static Optional<ConstraintGroup> of(Env env, ValidationInfo vi,
			VariableElement field) {
		return of(env, vi, field, field.asType());
	}

	static Optional<ConstraintGroup> of(Env env, ValidationInfo vi,
			Element member, TypeMirror type) {
		List<ConstraintInfo> list = member.getAnnotationMirrors().stream()
				.flatMap(am -> env.repos.find(am)).collect(Collectors.toList());
		if (cascading(env, vi, type, list)) {
			list.add(new ConstraintInfo(env, env.repos.dummyCascade(),
					new CascadeEmitter()));
		}

		if (list.isEmpty()) {
			return Optional.empty();
		}
		Collections.sort(list, (l, r) -> Integer.compare(l.order, r.order));
		VariableConstraintGroup result = new VariableConstraintGroup(env,
				member, type);
		result.constraints = list;
		return Optional.of(result);
	}

	static final Set<String> notCascading = new HashSet<>();
	static {
		notCascading.add(Boolean.class.getName());
		notCascading.add(Double.class.getName());
		notCascading.add(Float.class.getName());
		notCascading.add(Byte.class.getName());
		notCascading.add(Character.class.getName());
		notCascading.add(Short.class.getName());
		notCascading.add(Integer.class.getName());
		notCascading.add(Long.class.getName());
		notCascading.add(String.class.getName());
		notCascading.add(StringBuilder.class.getName());
		notCascading.add(StringBuffer.class.getName());
		notCascading.add(Date.class.getName());
		notCascading.add(Timestamp.class.getName());
		notCascading.add(Calendar.class.getName());
		notCascading.add(LocalDate.class.getName());
		notCascading.add(LocalTime.class.getName());
		notCascading.add(ZonedDateTime.class.getName());
		notCascading.add(OffsetTime.class.getName());
		notCascading.add(OffsetDateTime.class.getName());
	}

	static boolean cascading(Env env, ValidationInfo vi, TypeMirror type,
			List<ConstraintInfo> list) {
		if (vi.cascading == false) {
			return false;
		}
		if (type.getKind().isPrimitive()) {
			return false;
		}
		if (notCascading.contains(env.elemUtils.getClassName(type))) {
			return false;
		}
		return list.stream().noneMatch(
				ci -> ci.emitter instanceof CascadeEmitter);
	}

	@Override
	protected void emitBody(RoundEnvironment roundEnv, PrintWriter pw) {
		Name sn = member.getSimpleName();
		String targetType = this.env.elemUtils.toBoxedClassName(this.type);
		pw.printf("%s<%s<%s>> constraints = new %s<>();%n",
				env.classUtils.toSimpleName(List.class),
				env.classUtils.toSimpleName(Constraint.class), targetType,
				env.classUtils.toSimpleName(ArrayList.class));

		this.constraints.forEach(ci -> {
			pw.println("{");
			if (ci.emitter.emit(env, roundEnv, pw, ci.constraint, this.member,
					this.type)) {
				pw.println("constraints.add(c);");
			}
			pw.println("}");
		});
		String par = member.getKind() == ElementKind.METHOD ? "()" : "";
		pw.printf(
				"validations.add((v, c) -> validate(v.%s%s, constraints, newContext(c, \"%s\")));",
				sn, par, sn);
	}

	@Override
	public String toString() {
		return "VariableConstraintGroup [type=" + type + ", constraints="
				+ constraints + "]";
	}
}
