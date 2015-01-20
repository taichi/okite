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
package ninja.siden.okite.compiler.internal;

import java.lang.annotation.Annotation;
import java.util.stream.Stream;

import javax.annotation.processing.ProcessingEnvironment;

import ninja.siden.okite.annotation.Cascade;
import ninja.siden.okite.annotation.Emitter;
import ninja.siden.okite.annotation.Future;
import ninja.siden.okite.annotation.Implements;
import ninja.siden.okite.annotation.MapValidation;
import ninja.siden.okite.annotation.Max;
import ninja.siden.okite.annotation.Min;
import ninja.siden.okite.annotation.NotNull;
import ninja.siden.okite.annotation.Past;
import ninja.siden.okite.annotation.Pattern;
import ninja.siden.okite.annotation.Range;
import ninja.siden.okite.annotation.Size;
import ninja.siden.okite.annotation.Validate;
import ninja.siden.okite.annotation.Validation;

/**
 * @author taichi
 */
public class BuiltIns {

	final ProcessingEnvironment env;

	public BuiltIns(ProcessingEnvironment env) {
		this.env = env;
	}

	public Stream<Class<? extends Annotation>> validation() {
		return Stream.of(Validation.class, MapValidation.class);
	}

	public Stream<Class<? extends Annotation>> metaConstraint() {
		return Stream.of(Implements.class, Emitter.class);
	}

	public Stream<Class<? extends Annotation>> constraint() {
		// TODO read from options
		return Stream.of(Cascade.class, Max.class, Min.class, NotNull.class,
				Future.class, Past.class, Pattern.class, Range.class,
				Size.class, Validate.class);
	}
}
