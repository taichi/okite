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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ninja.siden.okite.ValidationContext;
import ninja.siden.okite.Violation;

/**
 * @author taichi
 */
public abstract class RangeConstraint<V> extends DefaultConstraint<V> {

	long min = 0L;

	long max = Long.MAX_VALUE;

	boolean inclusive = true;

	public long min() {
		return this.min;
	}

	public void min(long value) {
		this.min = value;
	}

	public long max() {
		return this.max;
	}

	public void max(long value) {
		this.max = value;
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
				Arrays.asList(this.min(), this.max())));
	}

	protected abstract boolean validate(V actual);

	public static class ForDouble<T> extends RangeConstraint<Double> {
		@Override
		protected boolean validate(Double actual) {
			return MinConstraint.validate(this.min(), actual, this.inclusive())
					&& MaxConstraint.validate(this.max(), actual,
							this.inclusive());
		}
	}

	public static class ForFloat<T> extends RangeConstraint<Float> {
		@Override
		protected boolean validate(Float actual) {
			return MinConstraint.validate(this.min(), actual, this.inclusive())
					&& MaxConstraint.validate(this.max(), actual,
							this.inclusive());
		}
	}

	public static class ForBigDecimal<T extends BigDecimal> extends
			RangeConstraint<T> {
		@Override
		protected boolean validate(T actual) {
			return MinConstraint.validate(this.min(), actual, this.inclusive())
					&& MaxConstraint.validate(this.max(), actual,
							this.inclusive());
		}
	}

	public static class ForBigInteger<T extends BigInteger> extends
			RangeConstraint<T> {
		@Override
		protected boolean validate(T actual) {
			return MinConstraint.validate(this.min(), actual, this.inclusive())
					&& MaxConstraint.validate(this.max(), actual,
							this.inclusive());
		}
	}

	public static class ForNumber<T extends Number> extends RangeConstraint<T> {
		@Override
		protected boolean validate(T actual) {
			return MinConstraint.validate(this.min(), actual, this.inclusive())
					&& MaxConstraint.validate(this.max(), actual,
							this.inclusive());
		}
	}

	public static class ForCharSequence<T extends CharSequence> extends
			RangeConstraint<T> {
		@Override
		protected boolean validate(T actual) {
			return MinConstraint.validate(this.min(), actual, this.inclusive())
					&& MaxConstraint.validate(this.max(), actual,
							this.inclusive());
		}
	}
}
