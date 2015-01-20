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
package ninja.siden.okite.internal;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import ninja.siden.okite.Constraint;
import ninja.siden.okite.Constraint.Policy;
import ninja.siden.okite.SimpleMessageResolver;
import ninja.siden.okite.Violation;
import ninja.siden.okite.constraint.MaxConstraint;

import org.junit.Before;
import org.junit.Test;

/**
 * @author taichi
 */
public class BaseValidatorTest {

	BaseValidator<Integer> target;

	MaxConstraint.ForNumber<Integer> first;

	MaxConstraint.ForNumber<Integer> second;

	@Before
	public void setUp() throws Exception {
		this.target = new BaseValidator<Integer>(new SimpleMessageResolver()) {
		};
		List<Constraint<Integer>> consts = new ArrayList<>();
		this.first = new MaxConstraint.ForNumber<>();
		first.value(9);
		first.order(10);
		first.messageId("okite.max");
		consts.add(this.first);

		this.second = new MaxConstraint.ForNumber<>();
		second.value(13);
		second.order(20);
		second.messageId("okite.max");
		consts.add(this.second);

		this.target.validations.add((v, c) -> this.target.validate(v, consts,
				new DefaultValidationContext("id")));
	}

	@Test
	public void continueOnError() {
		first.policy(Policy.ContinueOnError);
		second.policy(Policy.ContinueOnError);

		List<Violation> result = this.target.validate(14);
		assertEquals(2, result.size());
	}

	@Test
	public void stopOnError() throws Exception {
		first.policy(Policy.StopOnError);
		second.policy(Policy.ContinueOnError);

		List<Violation> result = this.target.validate(14);
		assertEquals(1, result.size());
	}

	@Test
	public void continueToNextTarget() throws Exception {
		first.policy(Policy.ContinueToNextTarget);
		second.policy(Policy.ContinueOnError);

		List<Constraint<Integer>> consts = new ArrayList<>();
		MaxConstraint.ForNumber<Integer> third = new MaxConstraint.ForNumber<>();
		third.value(18);
		third.order(20);
		third.messageId("okite.max");
		consts.add(third);

		this.target.validations.add((v, c) -> this.target.validate(v, consts,
				new DefaultValidationContext("id")));

		List<Violation> result = this.target.validate(19);
		assertEquals(2, result.size());
	}
}
