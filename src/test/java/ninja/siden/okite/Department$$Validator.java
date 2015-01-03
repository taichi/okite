package ninja.siden.okite;

import java.util.SortedSet;
import java.util.TreeSet;

import ninja.siden.okite.constraint.MinConstraint;
import ninja.siden.okite.internal.BaseValidator;
import ninja.siden.okite.internal.DefaultValidationContext;

// to be auto generation
public class Department$$Validator extends BaseValidator<Department> {

	public Department$$Validator(MessageResolver resolver) {
		_id(resolver);
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
}
