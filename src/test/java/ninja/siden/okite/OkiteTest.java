/*
 * Copyright 2014 SATO taichi
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

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * @author taichi
 */
public class OkiteTest {

	Validator<Employee> target;

	@Before
	public void setUp() {
		MessageResolver resolver = new SimpleMessageResolver();
		target = new _Employee$$Validator(resolver);

	}

	Employee make() {
		Employee employee = new Employee();
		employee.id = 3;
		employee.name = "aabb";
		employee.dept = new Department();
		employee.dept.id = 7;
		employee.subProjects = new Project[0];
		return employee;
	}

	@Test
	public void test_id() {
		Employee employee = make();
		employee.id = -1;

		List<Violation> errors = target.validate(employee);
		Violation v = errors.get(0);
		assertEquals("more than 0", v.toMessage());
	}

	@Test
	public void test_name_null() throws Exception {
		Employee employee = make();
		employee.name = null;

		List<Violation> errors = target.validate(employee);
		Violation v = errors.get(0);
		assertEquals("not to be null", v.toMessage());
		assertEquals("name", v.target());
	}

	@Test
	public void test_name_pattern() throws Exception {
		Employee employee = make();
		employee.name = "3355";

		List<Violation> errors = target.validate(employee);
		Violation v = errors.get(0);
		assertEquals("must match [a-z]+", v.toMessage());
		assertEquals("name", v.target());
	}

	@Test
	public void test_dept_id_minus() throws Exception {
		Employee employee = make();
		employee.dept.id = -1;
		List<Violation> errors = target.validate(employee);
		Violation v = errors.get(0);
		assertEquals("more than 0", v.toMessage());
		assertEquals("dept.id", v.target());
	}
}
