package ninja.siden.okite;

import java.util.ArrayList;
import java.util.List;

import ninja.siden.okite.Constraint.Policy;
import ninja.siden.okite.constraint.CascadeConstraint;
import ninja.siden.okite.constraint.MaxConstraint;
import ninja.siden.okite.constraint.MinConstraint;
import ninja.siden.okite.constraint.NotNullConstraint;
import ninja.siden.okite.constraint.PatternConstraint;
import ninja.siden.okite.constraint.RangeConstraint;
import ninja.siden.okite.internal.BaseValidator;

// to be auto generation
public class _Employee$$Validator extends BaseValidator<Employee> {

	public _Employee$$Validator(MessageResolver resolver) {
		super(resolver);
	}

	{
		List<Constraint<Integer>> constraints = new ArrayList<>();
		{
			MinConstraint.ForNumber<Integer> c = new MinConstraint.ForNumber<>();
			c.messageId("okite.min");
			c.order(100);
			c.value(0);
			constraints.add(c);
		}
		{
			MaxConstraint.ForNumber<Integer> c = new MaxConstraint.ForNumber<>();
			c.messageId("okite.max");
			c.order(99);
			c.value(55);
			constraints.add(c);
		}

		validations.add((v, c) -> validate(v.id, constraints,
				newContext(c, "id")));
	}

	{
		List<Constraint<String>> constraints = new ArrayList<>();
		{
			NotNullConstraint<String> c = new NotNullConstraint<>();
			c.messageId("okite.notnull");
			c.order(10);
			constraints.add(c);
		}
		{
			PatternConstraint<String> c = new PatternConstraint<>();
			c.messageId("okite.pattern");
			c.order(20);
			c.value("[a-z]+");
			constraints.add(c);
		}
		validations.add((v, c) -> validate(v.name, constraints,
				newContext(c, "name")));
	}

	{
		validations.add((v, c) -> convert(Policy.ContinueToNextTarget,
				v.validate(newContext(c, "validate"))));
	}

	{
		List<Constraint<Department>> constraints = new ArrayList<>();
		{
			NotNullConstraint<Department> c = new NotNullConstraint<>();
			c.messageId("okite.notnull");
			c.order(10);
			constraints.add(c);
		}
		{
			CascadeConstraint<Department> c = new CascadeConstraint<>(
					new Department$$Validator(resolver));
			c.order(20);
			constraints.add(c);
		}

		validations.add((v, c) -> validate(v.dept, constraints,
				newContext(c, "dept")));
	}

	{
		List<Constraint<ninja.siden.okite.Project[]>> constraints = new ArrayList<>();
		{
			CascadeConstraint.ForArray<ninja.siden.okite.Project, ninja.siden.okite.Project[]> c = new CascadeConstraint.ForArray<Project, Project[]>(
					new Project$$Validator(resolver));
			constraints.add(c);
		}
		validations.add((v, c) -> validate(v.subProjects, constraints,
				newContext(c, "subProjects")));
	}// END _subProjects

	{
		List<Constraint<Long>> constraints = new ArrayList<>();
		{
			RangeConstraint<Long> c = new RangeConstraint.ForNumber<>();
			c.min(3L);
			c.max(5L);
			c.inclusive(true);
			constraints.add(c);
		}

		validations.add((v, c) -> validate(v.combo(), constraints,
				newContext(c, "combo")));
	}
}
