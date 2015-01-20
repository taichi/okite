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
package ninja.siden.okite.compiler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import ninja.siden.okite.compiler.internal.ValidatorWriter;

/**
 * @author taichi
 */
public class ValidationProcessor extends AbstractProcessor {

	Env env;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		this.env = new Env(processingEnv);
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			javax.annotation.processing.RoundEnvironment originalRE) {
		if (originalRE.processingOver()) {
			return false;
		}
		RoundEnvironment roundEnv = new RoundEnvironment(env, originalRE);

		roundEnv.getTargets().map(vi -> ValidatorWriter.of(env, vi))
				.forEach(vw -> {
					vw.write(env, roundEnv);
				});
		return false;
	}

	@Override
	public Set<String> getSupportedOptions() {
		return Stream.of(Options.Kind.values()).map(Options.Kind::toOption)
				.collect(Collectors.toSet());
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return new HashSet<>(Arrays.asList("*"));
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.RELEASE_8;
	}
}
