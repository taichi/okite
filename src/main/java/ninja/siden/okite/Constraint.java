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
package ninja.siden.okite;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.Optional;

/**
 * @author taichi
 */
public interface Constraint<V> extends Comparable<Constraint<V>> {

	Optional<Violation> validate(V value, ValidationContext context);

	String messageId();

	Constraint<V> messageId(String id);

	int order();

	Constraint<V> order(int order);

	@Override
	default int compareTo(Constraint<V> o) {
		return Integer.compare(order(), o.order());
	}

	@Target(ElementType.ANNOTATION_TYPE)
	public @interface Implements {
		@SuppressWarnings("rawtypes")
		Class<? extends Constraint> value();
	}
}