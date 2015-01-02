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
package ninja.siden.okite.internal;

import java.util.List;

import ninja.siden.okite.MessageResolver;
import ninja.siden.okite.ValidationContext;
import ninja.siden.okite.Violation;

/**
 * @author taichi
 */
public class DefaultValidationContext implements ValidationContext {

	final MessageResolver resolver;
	final String target;

	public DefaultValidationContext(MessageResolver resolver, String target) {
		this.resolver = resolver;
		this.target = target;
	}

	@Override
	public Violation to(String messageId) {
		return new DefaultViolation(this.resolver, this.target, messageId);
	}

	@Override
	public Violation to(String messageId, List<?> args) {
		return new DefaultViolation(this.resolver, this.target, messageId, args);
	}

}
