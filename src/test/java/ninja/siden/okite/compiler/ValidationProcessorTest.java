package ninja.siden.okite.compiler;

import static org.junit.Assert.assertTrue;
import io.gige.CompilationResult;
import io.gige.CompilerContext;
import io.gige.Compilers;
import io.gige.junit.CompilerRunner;
import ninja.siden.okite.Department;
import ninja.siden.okite.Employee;
import ninja.siden.okite.compiler.test.MyConst;
import ninja.siden.okite.compiler.test.MyValidation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author taichi
 */
@RunWith(CompilerRunner.class)
public class ValidationProcessorTest {

	@Compilers
	CompilerContext context;

	@Before
	public void setUp() throws Exception {
		this.context.setSourcePath("src/test/java")
				.set(diag -> System.out.println(diag))
				.set(new ValidationProcessor());

	}

	@Test
	public void test() throws Exception {
		CompilationResult result = this.context.setUnits(Employee.class,
				Department.class, MyValidation.class, MyConst.class).compile();

		assertTrue(result.success());
		System.out.println("==============");
	}

}
