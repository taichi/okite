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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ninja.siden.okite.Constraint;
import ninja.siden.okite.Constraint.Policy;
import ninja.siden.okite.MessageResolver;
import ninja.siden.okite.ValidationContext;
import ninja.siden.okite.Validator;
import ninja.siden.okite.Violation;

/**
 * @author taichi
 */
public abstract class BaseValidator<T> implements Validator<T> {

	protected MessageResolver resolver;
	protected List<BiFunction<T, ValidationContext, Progress>> validations = new ArrayList<>();

	protected BaseValidator(MessageResolver resolver) {
		this.resolver = resolver;
	}

	@Override
	public List<Violation> validate(T value) {
		return validate(value, newContext(""));
	}

	@Override
	public List<Violation> validate(T value, ValidationContext context) {
		List<Violation> result = new ArrayList<>();
		for (BiFunction<T, ValidationContext, Progress> fn : validations) {
			if (fn.apply(value, context).addTo(result).stopOnError()) {
				break;
			}
		}
		return result;
	}

	protected <V> Progress validate(V value,
			Collection<Constraint<V>> constraints, ValidationContext context) {
		Progress p = new Progress();
		for (Constraint<V> cons : constraints) {
			p.add(cons.validate(value, context));
			if (p.continueOnError(cons.policy()) == false) {
				return p.cause(cons.policy());
			}
		}
		return p;
	}

	protected ValidationContext newContext(ValidationContext parent,
			String target) {
		String pT = parent.target();
		return newContext(pT == null || pT.isEmpty() ? target : pT + "."
				+ target);
	}

	protected ValidationContext newContext(String target) {
		return new DefaultValidationContext(this.resolver, target);
	}

	protected Progress convert(Policy p, Violation[] result) {
		return new Progress(p, Arrays.asList(result));
	}

	protected Progress convert(Policy p, Collection<Violation> result) {
		return new Progress(p, new ArrayList<>(result));
	}

	protected Progress convert(Policy p, Stream<Violation> result) {
		return new Progress(p, result.collect(Collectors.toList()));
	}

}
