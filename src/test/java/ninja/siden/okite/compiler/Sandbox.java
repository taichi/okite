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

import static org.junit.Assert.assertTrue;
import io.gige.CompilationResult;
import io.gige.CompilerContext;
import io.gige.Compilers;
import io.gige.Compilers.Type;
import io.gige.junit.CompilerRunner;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Types;

import ninja.siden.okite.Employee;
import ninja.siden.okite.constraint.MaxConstraint;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author taichi
 */
@RunWith(CompilerRunner.class)
public class Sandbox {

	@Compilers(Type.Eclipse)
	CompilerContext context;

	@Before
	public void setUp() throws Exception {
		this.context.setSourcePath("src/test/java")
				.set(diag -> System.err.println(diag)).setUnits(Employee.class);
	}

	@Test
	public void testName() throws Exception {
		CompilationResult result = this.context.compile();
		assertTrue(result.success());

	}

	@Ignore
	@Test
	public void primitiveMirror() throws Exception {
		CompilationResult result = this.context.compile();
		ProcessingEnvironment env = result.getEnvironment();
		Types type = env.getTypeUtils();
		javax.lang.model.util.Elements elms = env.getElementUtils();

		PrimitiveType primitive = type.getPrimitiveType(TypeKind.DOUBLE);
		TypeElement box = elms.getTypeElement("java.lang.Double");
		TypeElement te2 = type.boxedClass(primitive);
		assertTrue(type.isSameType(te2.asType(), box.asType()));

		TypeElement number = elms.getTypeElement("java.lang.Number");
		assertTrue(type.isAssignable(te2.asType(), number.asType()));
		System.out.println(MaxConstraint.ForNumber.class.getCanonicalName());
	}
}
