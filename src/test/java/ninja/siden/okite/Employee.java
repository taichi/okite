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
package ninja.siden.okite;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;
import javax.annotation.Resource;

import ninja.siden.okite.Constraint.Policy;
import ninja.siden.okite.annotation.AnnotateWith;
import ninja.siden.okite.annotation.AnnotateWith.AnnotationTarget;
import ninja.siden.okite.annotation.Cascade;
import ninja.siden.okite.annotation.Max;
import ninja.siden.okite.annotation.Min;
import ninja.siden.okite.annotation.NotNull;
import ninja.siden.okite.annotation.Pattern;
import ninja.siden.okite.annotation.Size;
import ninja.siden.okite.annotation.Validate;
import ninja.siden.okite.annotation.Validation;
import ninja.siden.okite.compiler.test.MyConst;

/**
 * @author taichi
 */
@Validation(cascading = true, with = {
		@AnnotateWith(value = Resource.class, target = AnnotationTarget.TYPE),
		// @AnnotateWith(value = Validation.class, target =
		// AnnotationTarget.TYPE),
		@AnnotateWith(value = Generated.class, values = "value={\"aaa\",\"bbb\"}") })
public class Employee {

	@Min(0)
	@Max
	int id;

	@NotNull(order = 10, policy = Policy.ContinueOnError)
	@Pattern(value = "[a-z]+", order = 20, policy = Policy.ContinueToNextTarget)
	@Pattern(value = "[0-9]+", order = 15, policy = Policy.StopOnError)
	@Size(max = 10, order = 13)
	String name;

	@Validate
	public List<Violation> validate(ValidationContext context) {
		List<Violation> result = new ArrayList<>();
		if (this.name != null && "aazz".equals(this.name)) {
			result.add(context.to("okite.default", Arrays.asList("violation")));
		}
		return result;
	}

	public List<Violation> argsMissed(ValidationContext context, String name) {
		return null;
	}

	public String retunTypeMissed(ValidationContext context) {
		return null;
	}

	@Resource
	public List<Violation> missAnnotation(ValidationContext context) {
		return null;
	}

	static int CONST = 10;

	@NotNull(order = 10)
	@Cascade(order = 20, value = false)
	Department dept;

	@NotNull(order = 10)
	Department dept2;

	@Cascade
	Project prj;

	@NotNull
	Project[] subProjects;

	@NotNull
	List<Project> subSubProj;

	@Size(min = 1)
	Map<String, Project> mapping;

	@MyConst
	Long combo() {
		return 1L;
	}

	@Resource
	BigDecimal nonValidate;

}