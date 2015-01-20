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
package ninja.siden.okite.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ninja.siden.okite.Constraint.Policy;

/**
 * The annotated element size must be between the specified boundaries
 * (included).
 * <ul>
 * <li>CharSequence</li>
 * <li>Collection</li>
 * <li>Map</li>
 * <li>Array</li>
 * </ul>
 * 
 * @author taichi
 */
@Repeatable(Size.List.class)
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Emitter("ninja.siden.okite.compiler.emitter.SizeEmitter")
public @interface Size {

	int min() default 0;

	int max() default Integer.MAX_VALUE;

	boolean inclusive() default true;

	String messageId() default "okite.size";

	int order() default 0;

	Policy policy() default Policy.ContinueToNextTarget;

	@Target({ ElementType.FIELD, ElementType.METHOD })
	public @interface List {
		Size[] value();
	}
}
