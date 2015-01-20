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
package ninja.siden.okite.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ninja.siden.okite.Constraint.Policy;
import ninja.siden.okite.Violation;

/**
 * @author taichi
 */
public class Progress {
	List<Violation> progress = new ArrayList<>();
	Optional<Policy> cause = Optional.empty();

	public Progress() {
		this.progress = new ArrayList<>();
	}

	public Progress(Policy policy, List<Violation> list) {
		this.progress = list;
		this.cause = list.isEmpty() ? Optional.empty() : Optional.of(policy);
	}

	public Progress add(List<Violation> vios) {
		this.progress.addAll(vios);
		return this;
	}

	public Progress addTo(List<Violation> container) {
		container.addAll(this.progress);
		return this;
	}

	public Progress cause(Policy policy) {
		this.cause = Optional.of(policy);
		return this;
	}

	public boolean continueOnError(Policy policy) {
		return this.progress.isEmpty() || Policy.ContinueOnError.equals(policy);
	}

	public boolean stopOnError() {
		return this.progress.isEmpty() == false
				&& cause.map(p -> p == Policy.StopOnError).orElse(false);
	}
}
