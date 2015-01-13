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
import java.util.Map;
import java.util.stream.Stream;

import ninja.siden.okite.ValidationContext;
import ninja.siden.okite.Violation;

/**
 * @author taichi
 */
public abstract class SizeConstraint<V> extends DefaultConstraint<V> {

	int min;

	int max;

	public SizeConstraint<V> min(int value) {
		this.min = value;
		return this;
	}

	public int min() {
		return this.min;
	}

	public SizeConstraint<V> max(int value) {
		this.max = value;
		return this;
	}

	public int max() {
		return this.max;
	}

	@Override
	public Stream<Violation> validate(V value, ValidationContext context) {
		if (value == null) {
			return Stream.empty();
		}
		int size = getSize(value);
		if (size < this.min() || this.max() < size) {
			return Stream.of(context.to(messageId(),
					Arrays.asList(this.min(), this.max())));
		}

		return Stream.empty();
	}

	protected abstract int getSize(V value);

	public static class CharSequenceSize extends SizeConstraint<CharSequence> {
		@Override
		protected int getSize(CharSequence value) {
			return value.length();
		}
	}

	public static class CollectionSize extends SizeConstraint<Collection<?>> {
		@Override
		protected int getSize(Collection<?> value) {
			return value.size();
		}
	}

	public static class MapSize extends SizeConstraint<Map<?, ?>> {
		@Override
		protected int getSize(Map<?, ?> value) {
			return value.size();
		}
	}

	public static class ArraySize extends SizeConstraint<Object> {
		@Override
		protected int getSize(Object value) {
			return Array.getLength(value);
		}
	}

}
