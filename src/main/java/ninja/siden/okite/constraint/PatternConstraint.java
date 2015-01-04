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

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;

import ninja.siden.okite.ValidationContext;
import ninja.siden.okite.Violation;

/**
 * @author taichi
 */
public class PatternConstraint<V extends CharSequence> extends
		DefaultConstraint<V> {

	Pattern pattern;

	int flags;

	public PatternConstraint<V> pattern(String pattern) {
		this.pattern = Pattern.compile(pattern, this.flags());
		return this;
	}

	public String pattern() {
		return this.pattern.pattern();
	}

	public PatternConstraint<V> flags(int value) {
		this.flags = value;
		this.pattern = Pattern.compile(this.pattern(), value);
		return this;
	}

	public int flags() {
		return this.flags;
	}

	@Override
	public Optional<Violation> validate(V t, ValidationContext context) {
		if (t == null || pattern.matcher(t).matches()) {
			return Optional.empty();
		}
		return Optional.of(context.to(this.messageId(),
				Arrays.asList(pattern.pattern())));
	}
}
