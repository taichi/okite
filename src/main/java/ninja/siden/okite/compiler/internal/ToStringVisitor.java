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

import java.util.List;
import java.util.stream.Collectors;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.AbstractAnnotationValueVisitor8;

import ninja.siden.okite.compiler.Env;

/**
 * @author taichi
 */
public class ToStringVisitor extends
		AbstractAnnotationValueVisitor8<StringBuilder, StringBuilder> {

	final Env env;
	final TypeMirror returnType;

	public ToStringVisitor(ExecutableElement key, Env env) {
		this.returnType = key.getReturnType();
		this.env = env;

	}

	@Override
	public StringBuilder visitBoolean(boolean b, StringBuilder p) {
		return p.append(b);
	}

	@Override
	public StringBuilder visitDouble(double d, StringBuilder p) {
		if (Double.isNaN(d)) {
			return p.append("Double.NaN");
		}
		if (Double.POSITIVE_INFINITY == d) {
			return p.append("Double.POSITIVE_INFINITY");
		}
		if (Double.NEGATIVE_INFINITY == d) {
			return p.append("Double.NEGATIVE_INFINITY");
		}
		if (Double.MAX_VALUE == d) {
			return p.append("Double.MAX_VALUE");
		}
		if (Double.MIN_VALUE == d) {
			return p.append("Double.MIN_VALUE");
		}
		if (Double.MIN_NORMAL == d) {
			return p.append("Double.MIN_NORMAL");
		}
		return p.append(Double.toString(d));
	}

	@Override
	public StringBuilder visitFloat(float f, StringBuilder p) {
		if (Float.isNaN(f)) {
			return p.append("Float.NaN");
		}
		if (Float.POSITIVE_INFINITY == f) {
			return p.append("Float.POSITIVE_INFINITY");
		}
		if (Float.NEGATIVE_INFINITY == f) {
			return p.append("Float.NEGATIVE_INFINITY");
		}
		if (Float.MAX_VALUE == f) {
			return p.append("Float.MAX_VALUE");
		}
		if (Float.MIN_VALUE == f) {
			return p.append("Float.MIN_VALUE");
		}
		if (Float.MIN_NORMAL == f) {
			return p.append("Float.MIN_NORMAL");
		}
		return p.append(Float.toString(f));
	}

	@Override
	public StringBuilder visitByte(byte b, StringBuilder p) {
		if (Byte.MIN_VALUE == b) {
			return p.append("Byte.MIN_VALUE");
		}
		if (Byte.MAX_VALUE == b) {
			return p.append("Byte.MAX_VALUE");
		}
		return p.append(Byte.toString(b));
	}

	@Override
	public StringBuilder visitChar(char c, StringBuilder p) {
		if (Character.MIN_VALUE == c) {
			return p.append("Character.MIN_VALUE");
		}
		if (Character.MAX_VALUE == c) {
			return p.append("Character.MAX_VALUE");
		}
		return p.append(Character.toString(c));
	}

	@Override
	public StringBuilder visitShort(short s, StringBuilder p) {
		if (Short.MIN_VALUE == s) {
			return p.append("Short.MIN_VALUE");
		}
		if (Short.MAX_VALUE == s) {
			return p.append("Short.MAX_VALUE");
		}
		return p.append(String.valueOf(s));
	}

	@Override
	public StringBuilder visitInt(int i, StringBuilder p) {
		if (Integer.MIN_VALUE == i) {
			return p.append("Integer.MIN_VALUE");
		}
		if (Integer.MAX_VALUE == i) {
			return p.append("Integer.MAX_VALUE");
		}
		return p.append(String.valueOf(i));
	}

	@Override
	public StringBuilder visitLong(long i, StringBuilder p) {
		if (Long.MIN_VALUE == i) {
			return p.append("Long.MIN_VALUE");
		}
		if (Long.MAX_VALUE == i) {
			return p.append("Long.MAX_VALUE");
		}
		return p.append(String.valueOf(i)).append('L');
	}

	@Override
	public StringBuilder visitString(String s, StringBuilder p) {
		return p.append('"').append(s).append('"');
	}

	@Override
	public StringBuilder visitType(TypeMirror t, StringBuilder p) {
		return p.append(env.elemUtils.getClassName(t));
	}

	@Override
	public StringBuilder visitEnumConstant(VariableElement c, StringBuilder p) {
		p.append(env.elemUtils.getClassName(c.asType()));
		p.append(".");
		return p.append(c.getSimpleName());
	}

	@Override
	public StringBuilder visitAnnotation(AnnotationMirror a, StringBuilder p) {
		// TODO not implemented
		return p;
	}

	@Override
	public StringBuilder visitArray(List<? extends AnnotationValue> vals,
			StringBuilder p) {
		p.append("new ");
		p.append(env.elemUtils.getClassName(returnType));
		p.append("{");
		String s = vals.stream().map(av -> {
			StringBuilder stb = new StringBuilder();
			av.accept(this, stb);
			return stb;
		}).collect(Collectors.joining(", "));
		p.append(s);
		return p.append("}");
	}
}