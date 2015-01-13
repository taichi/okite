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
package ninja.siden.okite.compiler.test;

import ninja.siden.okite.Constraint.Policy;
import ninja.siden.okite.annotation.Implements;
import ninja.siden.okite.constraint.RangeConstraint;

/**
 * @author taichi
 */
@Implements(RangeConstraint.class)
public @interface MyConst {

	long min() default 0L;

	long max() default Long.MAX_VALUE;

	String messageId() default "okite.range";

	int order() default 0;

	Policy policy() default Policy.ContinueToNextField;
}
