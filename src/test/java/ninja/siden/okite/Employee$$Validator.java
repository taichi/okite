package ninja.siden.okite;

import java.util.SortedSet;
import java.util.TreeSet;

import ninja.siden.okite.constraint.CascadeConstraint;
import ninja.siden.okite.constraint.MinConstraint;
import ninja.siden.okite.constraint.NotNullConstraint;
import ninja.siden.okite.constraint.PatternConstraint;
import ninja.siden.okite.constraint.RangeConstraint;
import ninja.siden.okite.internal.BaseValidator;
import ninja.siden.okite.internal.DefaultValidationContext;

// to be auto generation
public class Employee$$Validator extends BaseValidator<Employee> {

	public Employee$$Validator(MessageResolver resolver) {
		_id(resolver);
		_name(resolver);
		_validate(resolver);
		_dept(resolver);
		_combo(resolver);
	}

	private void _id(MessageResolver resolver) {
		SortedSet<Constraint<Integer>> constraints = new TreeSet<>();
		{
			MinConstraint<Integer> c = new MinConstraint<Integer>();
			c.messageId("okite.min");
			c.order(100);
			c.value(0);
			constraints.add(c);
		}

		validations.add(v -> validate(v.id, constraints,
				new DefaultValidationContext(resolver, "id")));
	}

	private void _name(MessageResolver resolver) {
		SortedSet<Constraint<String>> constraints = new TreeSet<>();
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
			c.pattern("[a-z]+");
			constraints.add(c);
		}
		validations.add(v -> validate(v.name, constraints,
				new DefaultValidationContext(resolver, "name")));
	}

	private void _validate(MessageResolver resolver) {
		validations.add(v -> convert(v.validate(new DefaultValidationContext(
				resolver, "validate"))));
	}

	private void _dept(MessageResolver resolver) {
		SortedSet<Constraint<Department>> constraints = new TreeSet<>();
		{
			NotNullConstraint<Department> c = new NotNullConstraint<>();
			c.messageId("okite.notnull");
			c.order(10);
			constraints.add(c);
		}
		{
			Validator<Department> cacade = new Department$$Validator(resolver);
			CascadeConstraint<Department> c = new CascadeConstraint<>(cacade);
			c.order(20);
			constraints.add(c);
		}

		validations.add(v -> validate(v.dept, constraints,
				new DefaultValidationContext(resolver, "dept")));
	}

	private void _combo(MessageResolver resolver) {
		SortedSet<Constraint<Integer>> constraints = new TreeSet<>();
		{
			RangeConstraint<Integer> c = new RangeConstraint<>();
			c.min(3);
			c.max(5);
			constraints.add(c);
		}

		validations.add(v -> validate(v.combo(), constraints,
				new DefaultValidationContext(resolver, "combo")));
	}
}
