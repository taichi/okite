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
package ninja.siden.okite.constraint;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import mockit.Mock;
import mockit.MockUp;
import ninja.siden.okite.ValidationContext;
import ninja.siden.okite.Violation;

import org.junit.Before;
import org.junit.Test;

/**
 * @author taichi
 */
public class MaxConstraintTest {

	MaxConstraint<Integer> target;

	@Before
	public void setUp() {
		this.target = new MaxConstraint<>();
		this.target.value(10);
	}

	@Test
	public void more() throws Exception {
		ValidationContext context = new MockUp<ValidationContext>() {
			@Mock(invocations = 1)
			public Violation to(String messageId, List<?> args) {
				return new MockUp<Violation>() {
				}.getMockInstance();
			}
		}.getMockInstance();

		Optional<Violation> opt = this.target.validate(11, context);
		assertTrue(opt.isPresent());
	}

	@Test
	public void equal() throws Exception {
		ValidationContext context = new MockUp<ValidationContext>() {
			@Mock(invocations = 0)
			public Violation to(String messageId, List<?> args) {
				return new MockUp<Violation>() {
				}.getMockInstance();
			}
		}.getMockInstance();

		Optional<Violation> opt = this.target.validate(10, context);
		assertFalse(opt.isPresent());
	}

	@Test
	public void less() throws Exception {
		ValidationContext context = new MockUp<ValidationContext>() {
			@Mock(invocations = 0)
			public Violation to(String messageId, List<?> args) {
				return new MockUp<Violation>() {
				}.getMockInstance();
			}
		}.getMockInstance();

		Optional<Violation> opt = this.target.validate(9, context);
		assertFalse(opt.isPresent());
	}
}
