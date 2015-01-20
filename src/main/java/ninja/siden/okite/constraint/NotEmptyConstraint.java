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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import ninja.siden.okite.ValidationContext;
import ninja.siden.okite.Violation;

/**
 * @author taichi
 */
public abstract class NotEmptyConstraint<V> extends DefaultConstraint<V> {

	@Override
	public List<Violation> validate(V value, ValidationContext context) {
		if (value == null || isNotEmpty(value)) {
			return Collections.emptyList();
		}
		return Arrays.asList(context.to(messageId()));
	}

	protected abstract boolean isNotEmpty(V value);

	public static class ForOptional<V extends Optional<?>> extends
			NotEmptyConstraint<V> {

		@Override
		protected boolean isNotEmpty(V value) {
			return value.isPresent();
		}
	}

	public static class ForCharSequence<V extends CharSequence> extends
			NotEmptyConstraint<V> {
		@Override
		protected boolean isNotEmpty(V value) {
			return 0 < value.length();
		}
	}

	public static class ForCollection<V extends Collection<?>> extends
			NotEmptyConstraint<V> {

		@Override
		protected boolean isNotEmpty(V value) {
			return value.isEmpty() == false;
		}
	}

	public static class ForMap<V extends Map<?, ?>> extends
			NotEmptyConstraint<V> {
		@Override
		protected boolean isNotEmpty(V value) {
			return value.isEmpty() == false;
		}
	}

	public static class ForArray<V> extends NotEmptyConstraint<V> {

		@Override
		protected boolean isNotEmpty(V value) {
			return 0 < Array.getLength(value);
		}
	}

}
