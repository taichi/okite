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

import java.util.Objects;

import ninja.siden.okite.Constraint;

/**
 * @author taichi
 */
public abstract class DefaultConstraint<V> implements Constraint<V> {

	String messageId = "";

	int order = 0;

	Policy policy = Policy.StopOnError;

	@Override
	public String messageId() {
		return this.messageId;
	}

	@Override
	public Constraint<V> messageId(String id) {
		this.messageId = Objects.requireNonNull(id);
		return this;
	}

	@Override
	public int order() {
		return this.order;
	}

	@Override
	public Constraint<V> order(int order) {
		this.order = order;
		return this;
	}

	@Override
	public ninja.siden.okite.Constraint.Policy policy() {
		return this.policy;
	}

	@Override
	public Constraint<V> policy(ninja.siden.okite.Constraint.Policy policy) {
		this.policy = Objects.requireNonNull(policy);
		return this;
	}
}
