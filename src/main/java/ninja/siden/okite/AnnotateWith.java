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

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author taichi
 */
@Repeatable(AnnotateWith.List.class)
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE })
public @interface AnnotateWith {

	Class<? extends Annotation> value();

	AnnotationTarget target() default AnnotationTarget.CONSTRUCTOR;

	String values() default "";

	public enum AnnotationTarget {
		TYPE,

		CONSTRUCTOR,

		CONSTRUCTOR_PARAMETER;
	}

	@Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE })
	public @interface List {
		AnnotateWith[] value();
	}
}