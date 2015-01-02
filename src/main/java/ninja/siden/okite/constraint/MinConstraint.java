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

import java.util.Optional;

import ninja.siden.okite.ValidationContext;
import ninja.siden.okite.Violation;

/**
 * @author taichi
 */
public class MinConstraint<V extends Number & Comparable<V>> extends
		DefaultConstraint<V> {

	protected V min;

	public V min() {
		return this.min;
	}

	public MinConstraint<V> min(V value) {
		this.min = value;
		return this;
	}

	@Override
	public Optional<Violation> validate(V value, ValidationContext context) {
		return 0 <= this.min.compareTo(value) ? Optional.empty() : Optional
				.of(context.to(this.messageId));
	}

}
