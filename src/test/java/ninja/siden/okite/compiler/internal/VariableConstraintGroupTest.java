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
import io.gige.util.ElementFilter;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.lang.model.element.TypeElement;

import ninja.siden.okite.compiler.BaseProcessor;
import ninja.siden.okite.compiler.RoundEnvironment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author taichi
 */
@RunWith(CompilerRunner.class)
public class VariableConstraintGroupTest {

	@Compilers
	CompilerContext context;

	@Before
	public void setUp() throws Exception {
		this.context.setSourcePath("src/test/java")
				.set(diag -> System.out.println(diag)).set(new ConsProcessor());
	}

	class ConsProcessor extends BaseProcessor {
		@Override
		public boolean process(Set<? extends TypeElement> annotations,
				javax.annotation.processing.RoundEnvironment originalRE) {
			if (originalRE.processingOver()) {
				return false;
			}
			new RoundEnvironment(env, originalRE);

			Optional<TypeElement> opt = env.elemUtils
					.getTypeElement(Cons.class);
			TypeElement type = opt.get();
			ValidationInfo vi = ValidationInfo.from(env, type);
			Stream<Optional<ConstraintGroup>> methods = ElementFilter
					.methodsIn(type).map(
							mtd -> VariableConstraintGroup.of(env, vi, mtd));
			Stream<Optional<ConstraintGroup>> fields = ElementFilter.fieldsIn(
					type).map(fld -> VariableConstraintGroup.of(env, vi, fld));
			List<ConstraintGroup> cgs = Stream.concat(methods, fields)
					.filter(Optional::isPresent).map(Optional::get)
					.collect(Collectors.toList());

			cgs.sort(ConstraintGroup.comparator(env));

			ConstraintGroup name = cgs.get(0);
			assertEquals("name", name.name());
			ConstraintGroup salary = cgs.get(1);
			assertEquals("salary", salary.name());
			ConstraintGroup id = cgs.get(2);
			assertEquals("id", id.name());

			return false;
		}
	}

	@Test
	public void test() throws Exception {
		CompilationResult result = this.context.setUnits(Cons.class).compile();
		assertTrue(result.success());

	}

}
