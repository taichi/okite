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
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.function.Function;
import java.util.stream.Stream;

import ninja.siden.okite.Constraint;
import ninja.siden.okite.ValidationContext;
import ninja.siden.okite.Validator;
import ninja.siden.okite.Violation;

/**
 * @author taichi
 */
public abstract class BaseValidator<T> implements Validator<T> {

	protected List<Function<T, Stream<Violation>>> validations = new ArrayList<>();

	@Override
	public Stream<Violation> validate(T value) {
		return validations.stream().map(fn -> fn.apply(value))
				.reduce(Stream.empty(), Stream::concat);
	}

	protected <V> Stream<Violation> validate(V value,
			SortedSet<Constraint<V>> constraints, ValidationContext context) {
		return constraints.stream().map(s -> s.validate(value, context))
				.filter(Optional::isPresent).map(Optional::get);
	}

	protected Stream<Violation> convert(Violation[] result) {
		return Stream.of(result);
	}

	protected Stream<Violation> convert(Collection<Violation> result) {
		return result.stream();
	}

	protected Stream<Violation> convert(Stream<Violation> result) {
		return result;
	}
}
