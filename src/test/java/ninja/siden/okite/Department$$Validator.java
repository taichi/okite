package ninja.siden.okite;

import java.util.ArrayList;
import java.util.List;

import ninja.siden.okite.constraint.MinConstraint;
import ninja.siden.okite.internal.BaseValidator;

// to be auto generation
public class Department$$Validator extends BaseValidator<Department> {

	public Department$$Validator(MessageResolver resolver) {
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

		validations.add((v, c) -> validate(v.id, constraints,
				newContext(c, "id")));
	}
}
