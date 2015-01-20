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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import io.gige.CompilationResult;
import io.gige.CompilerContext;
import io.gige.Compilers;
import io.gige.junit.CompilerRunner;

import java.util.List;
import java.util.Set;

import javax.annotation.Generated;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;

import ninja.siden.okite.annotation.AnnotateWith.AnnotationTarget;
import ninja.siden.okite.compiler.BaseProcessor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author taichi
 */
@RunWith(CompilerRunner.class)
public class ValidationInfoTest {

	@Compilers
	CompilerContext context;

	@Before
	public void setUp() throws Exception {
		this.context.setSourcePath("src/test/java").set(
				diag -> System.out.println(diag));
	}

	@Test
	public void validation() throws Exception {
		CompilationResult result = this.context.set(new BaseProcessor() {
			@Override
			public boolean process(Set<? extends TypeElement> annotations,
					RoundEnvironment roundEnv) {
				if (roundEnv.processingOver()) {
					return false;
				}

				TypeElement type = env.elemUtils.getTypeElement(Val.class)
						.get();

				List<? extends AnnotationMirror> list = type
						.getAnnotationMirrors();
				AnnotationMirror am = list.get(0);
				ValidationInfo info = ValidationInfo.from(env, type, am);
				assertEquals("pref", info.prefix);
				assertEquals("suff", info.suffix);
				assertTrue(info.cascading);

				assertEquals(2, info.with.size());
				AnnotateWithInfo awi = info.with.get(0);
				assertEquals(Generated.class.getName(), awi.annotation);
				assertEquals(AnnotationTarget.TYPE, awi.target);
				assertEquals("aaa=bbb", awi.attributes);

				return false;
			}
		}).setUnits(Val.class).compile();
		assertTrue(result.success());
	}

	@Test
	public void metaValidation() throws Exception {
		CompilationResult result = this.context.set(new BaseProcessor() {
			@Override
			public boolean process(Set<? extends TypeElement> annotations,
					RoundEnvironment roundEnv) {
				if (roundEnv.processingOver()) {
					return false;
				}

				TypeElement type = env.elemUtils.getTypeElement(MetaVal.class)
						.get();

				List<? extends AnnotationMirror> list = type
						.getAnnotationMirrors();
				AnnotationMirror am = list.get(0);
				ValidationInfo info = ValidationInfo.fromMeta(env, type, am);
				assertEquals("pref", info.prefix);
				assertEquals("suff", info.suffix);
				assertTrue(info.cascading);

				assertEquals(1, info.with.size());
				AnnotateWithInfo awi = info.with.get(0);
				assertEquals(Override.class.getName(), awi.annotation);
				assertEquals(AnnotationTarget.CONSTRUCTOR, awi.target);
				assertEquals("ccc=dddd", awi.attributes);

				return false;
			}
		}).setUnits(MetaVal.class).compile();
		assertTrue(result.success());
	}
}
