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
package ninja.siden.okite.constraint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import ninja.siden.okite.ValidationContext;
import ninja.siden.okite.Validator;
import ninja.siden.okite.Violation;
import ninja.siden.okite.internal.Progress;

/**
 * @author taichi
 */
public class CascadeConstraint<V> extends DefaultConstraint<V> {

	final Validator<V> delegate;

	public void value(boolean value) {
	}

	public CascadeConstraint(Validator<V> delegate) {
		this.delegate = delegate;
	}

	@Override
	public List<Violation> validate(V value, ValidationContext context) {
		return this.delegate.validate(value, context);
	}

	public static <V> List<Violation> validate(Validator<V> delegate,
			Iterable<V> values, Policy policy, ValidationContext context) {
		List<Violation> result = new ArrayList<>();
		Progress p = new Progress();
		for (V v : values) {
			p.add(delegate.validate(v, context));
			if (p.continueOnError(policy) == false || p.stopOnError()) {
				p.addTo(result);
				break;
			}
		}
		return result;
	}

	public static class ForArray<V, X> extends DefaultConstraint<V[]> {

		final Validator<V> delegate;

		public void value(boolean value) {
		}

		public ForArray(Validator<V> delegate) {
			this.delegate = delegate;
		}

		@Override
		public List<Violation> validate(V[] value, ValidationContext context) {
			return CascadeConstraint.validate(delegate, Arrays.asList(value),
					policy(), context);
		}
	}

	public static class ForIterable<V, T extends Iterable<V>> extends
			DefaultConstraint<T> {

		final Validator<V> delegate;

		public void value(boolean value) {
		}

		public ForIterable(Validator<V> delegate) {
			this.delegate = delegate;
		}

		@Override
		public List<Violation> validate(T value, ValidationContext context) {
			return CascadeConstraint.validate(delegate, value, policy(),
					context);
		}
	}

	public static class ForMap<V, T extends Map<?, V>> extends
			DefaultConstraint<T> {

		final Validator<V> delegate;

		public void value(boolean value) {
		}

		public ForMap(Validator<V> delegate) {
			this.delegate = delegate;
		}

		@Override
		public List<Violation> validate(T value, ValidationContext context) {
			return CascadeConstraint.validate(delegate, value.values(),
					policy(), context);
		}
	}

	public static class ForOptional<V, X> extends
			DefaultConstraint<Optional<V>> {
		final Validator<V> delegate;

		public void value(boolean value) {
		}

		public ForOptional(Validator<V> delegate) {
			this.delegate = delegate;
		}

		@Override
		public List<Violation> validate(Optional<V> value,
				ValidationContext context) {
			if (value == null) {
				return Collections.emptyList();
			}
			return value.map(v -> delegate.validate(v)).orElse(
					Collections.emptyList());
		}

	}
}
