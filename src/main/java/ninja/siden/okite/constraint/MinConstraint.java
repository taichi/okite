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

/**
 * @author taichi
 */
public class MinConstraint {

	public static boolean validate(long expected, Double actual,
			boolean inclusive) {
		if (actual == Double.NEGATIVE_INFINITY || actual.isNaN()) {
			return false;
		}
		if (actual == Double.POSITIVE_INFINITY) {
			return true;
		}
		return inclusive ? expected <= actual.longValue() : expected < actual
				.longValue();
	}

	public static boolean validate(long expected, Float actual,
			boolean inclusive) {
		if (actual == Float.NEGATIVE_INFINITY || actual.isNaN()) {
			return false;
		}
		if (actual == Float.POSITIVE_INFINITY) {
			return true;
		}
		return inclusive ? expected <= actual.longValue() : expected < actual
				.longValue();
	}

	public static <T extends BigDecimal> boolean validate(long expected,
			T actual, boolean inclusive) {
		int result = actual.compareTo(BigDecimal.valueOf(expected));
		return inclusive ? result < 1 : result < 0;
	}

	public static <T extends BigInteger> boolean validate(long expected,
			T actual, boolean inclusive) {
		int result = actual.compareTo(BigInteger.valueOf(expected));
		return inclusive ? result < 1 : result < 0;
	}

	public static <T extends Number> boolean validate(long expected, T actual,
			boolean inclusive) {
		return inclusive ? expected <= actual.longValue() : expected < actual
				.longValue();
	}

	public static <T extends CharSequence> boolean validate(long expected,
			T actual, boolean inclusive) {
		BigDecimal big = new BigDecimal(actual.toString());
		int result = big.compareTo(BigDecimal.valueOf(expected));
		return inclusive ? result < 1 : result < 0;
	}

	public static class ForDouble<T> extends BoundaryConstraint<Double> {
		@Override
		protected boolean validate(Double actual) {
			return MinConstraint.validate(this.value(), actual,
					this.inclusive());
		}
	}

	public static class ForFloat<T> extends BoundaryConstraint<Float> {
		@Override
		protected boolean validate(Float actual) {
			return MinConstraint.validate(this.value(), actual,
					this.inclusive());
		}
	}

	public static class ForBigDecimal<T extends BigDecimal> extends
			BoundaryConstraint<T> {
		@Override
		protected boolean validate(T actual) {
			int result = actual.compareTo(BigDecimal.valueOf(this.value()));
			return this.inclusive ? result < 1 : result < 0;
		}
	}

	public static class ForBigInteger<T extends BigInteger> extends
			BoundaryConstraint<T> {
		@Override
		protected boolean validate(T actual) {
			return MinConstraint.validate(this.value(), actual,
					this.inclusive());
		}
	}

	public static class ForNumber<T extends Number> extends
			BoundaryConstraint<T> {
		@Override
		protected boolean validate(T actual) {
			return MinConstraint.validate(this.value(), actual,
					this.inclusive());
		}
	}

	public static class ForCharSequence<T extends CharSequence> extends
			BoundaryConstraint<T> {
		@Override
		protected boolean validate(T actual) {
			return MinConstraint.validate(this.value(), actual,
					this.inclusive());
		}
	}
}
