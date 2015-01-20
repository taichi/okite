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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ninja.siden.okite.ValidationContext;
import ninja.siden.okite.Violation;

/**
 * @author taichi
 */
public abstract class BoundaryConstraint<V> extends DefaultConstraint<V> {

	long boundary = 0;

	boolean inclusive = true;

	public long value() {
		return this.boundary;
	}

	public void value(long value) {
		this.boundary = value;
	}

	public boolean inclusive() {
		return this.inclusive;
	}

	public void inclusive(boolean value) {
		this.inclusive = value;
	}

	@Override
	public List<Violation> validate(V value, ValidationContext context) {
		if (value == null || validate(value)) {
			return Collections.emptyList();
		}
		return Arrays.asList(context.to(this.messageId(),
				Arrays.asList(this.value())));
	}

	protected abstract boolean validate(V actual);

}
