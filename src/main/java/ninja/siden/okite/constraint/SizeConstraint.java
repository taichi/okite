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

import ninja.siden.okite.ValidationContext;
import ninja.siden.okite.Violation;

/**
 * @author taichi
 */
public abstract class SizeConstraint<V> extends DefaultConstraint<V> {

	int min;

	int max;

	boolean inclusive = true;

	public void min(int value) {
		this.min = value;
	}

	public int min() {
		return this.min;
	}

	public void max(int value) {
		this.max = value;
	}

	public int max() {
		return this.max;
	}

	public boolean inclusive() {
		return this.inclusive;
	}

	public void inclusive(boolean value) {
		this.inclusive = value;
	}

	@Override
	public List<Violation> validate(V value, ValidationContext context) {
		if (value == null) {
			return Collections.emptyList();
		}
		int size = getSize(value);
		if (size < this.min() || this.max() < size) {
			return Arrays.asList(context.to(messageId(),
					Arrays.asList(this.min(), this.max())));
		}

		return Collections.emptyList();
	}

	protected abstract int getSize(V value);

	public static class ForCharSequence<V extends CharSequence> extends
			SizeConstraint<V> {
		@Override
		protected int getSize(V value) {
			return value.length();
		}
	}

	public static class ForCollection<V extends Collection<?>> extends
			SizeConstraint<V> {
		@Override
		protected int getSize(V value) {
			return value.size();
		}
	}

	public static class ForMap<V extends Map<?, ?>> extends SizeConstraint<V> {
		@Override
		protected int getSize(V value) {
			return value.size();
		}
	}

	public static class ForArray<V> extends SizeConstraint<V> {
		@Override
		protected int getSize(V value) {
			return Array.getLength(value);
		}
	}

}
