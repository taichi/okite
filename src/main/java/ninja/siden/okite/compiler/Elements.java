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

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.SimpleElementVisitor8;
import javax.lang.model.util.SimpleTypeVisitor8;
import javax.lang.model.util.TypeKindVisitor8;
import javax.lang.model.util.Types;

/**
 * @author taichi
 */
public class Elements {

	final ProcessingEnvironment env;

	final javax.lang.model.util.Elements delegate;

	public Elements(ProcessingEnvironment env) {
		super();
		this.env = env;
		this.delegate = env.getElementUtils();
	}

	public Optional<TypeElement> getTypeElement(Class<?> clazz) {
		return getTypeElement(clazz.getCanonicalName());
	}

	public Optional<TypeElement> getTypeElement(String className) {
		try {
			return Optional.ofNullable(this.delegate.getTypeElement(className));
		} catch (NullPointerException e) {
			return Optional.empty();
		}
	}

	public Optional<TypeElement> toTypeElement(TypeMirror t) {
		Element e = env.getTypeUtils().asElement(t);
		if (e == null) {
			return Optional.empty();
		}
		return Optional.ofNullable(e.accept(
				new SimpleElementVisitor8<TypeElement, Void>() {
					@Override
					public TypeElement visitType(TypeElement e, Void p) {
						return e;
					}
				}, null));
	}

	public String getPackageName(TypeElement element) {
		PackageElement pkg = env.getElementUtils().getPackageOf(element);
		return pkg.getQualifiedName().toString();
	}

	public String getClassName(TypeMirror typeMirror) {
		StringBuilder p = typeMirror.accept(
				new TypeKindVisitor8<StringBuilder, StringBuilder>() {

					@Override
					public StringBuilder visitNoTypeAsVoid(NoType t,
							StringBuilder p) {
						return p.append("void");
					}

					@Override
					public StringBuilder visitPrimitive(PrimitiveType t,
							StringBuilder p) {
						return p.append(t.getKind().name().toLowerCase());
					}

					@Override
					public StringBuilder visitArray(ArrayType t, StringBuilder p) {
						t.getComponentType().accept(this, p);
						return p.append("[]");
					}

					@Override
					public StringBuilder visitDeclared(DeclaredType t,
							StringBuilder p) {
						toTypeElement(t).ifPresent(
								te -> p.append(te.getQualifiedName()));

						if (t.getTypeArguments().isEmpty() == false) {
							p.append("<");
							for (TypeMirror arg : t.getTypeArguments()) {
								arg.accept(this, p);
								p.append(", ");
							}
							p.setLength(p.length() - 2);
							p.append(">");
						}
						return p;
					}

					@Override
					public StringBuilder visitTypeVariable(TypeVariable t,
							StringBuilder p) {
						return p.append(t);
					}

					@Override
					public StringBuilder visitWildcard(WildcardType t,
							StringBuilder p) {
						p.append("?");
						TypeMirror extendsBound = t.getExtendsBound();
						if (extendsBound != null) {
							p.append(" extends ");
							extendsBound.accept(this, p);
						}
						TypeMirror superBound = t.getSuperBound();
						if (superBound != null) {
							p.append(" super ");
							superBound.accept(this, p);
						}
						return p;
					}

					@Override
					protected StringBuilder defaultAction(TypeMirror e,
							StringBuilder p) {
						p.append(e);
						throw new IllegalArgumentException(p.toString());
					}

				}, new StringBuilder());

		return p.length() > 0 ? p.toString() : Object.class.getName();
	}

	public String toBoxedClassName(TypeMirror typeMirror) {
		switch (typeMirror.getKind()) {
		case BOOLEAN:
			return Boolean.class.getSimpleName();
		case BYTE:
			return Byte.class.getSimpleName();
		case SHORT:
			return Short.class.getSimpleName();
		case INT:
			return Integer.class.getSimpleName();
		case LONG:
			return Long.class.getSimpleName();
		case FLOAT:
			return Float.class.getSimpleName();
		case DOUBLE:
			return Double.class.getSimpleName();
		case CHAR:
			return Character.class.getSimpleName();
		default:
			return getClassName(typeMirror);
		}
	}

	public Stream<? extends AnnotationMirror> findAnnotation(TypeMirror decl,
			Class<? extends Annotation> clazz) {
		Element type = env.getTypeUtils().asElement(decl);
		return findAnnotation(type, clazz);
	}

	public Stream<? extends AnnotationMirror> findAnnotation(Element element,
			Class<? extends Annotation> clazz) {
		Optional<TypeElement> opt = getTypeElement(clazz);
		if (opt.isPresent()) {
			return findAnnotation(element, opt.get());
		}
		return Stream.empty();
	}

	public Stream<? extends AnnotationMirror> findAnnotation(Element element,
			TypeElement findFor) {
		if (element == null || findFor == null) {
			return Stream.empty();
		}
		TypeMirror anon = findFor.asType();
		return element
				.getAnnotationMirrors()
				.stream()
				.filter(am -> env.getTypeUtils().isSameType(
						am.getAnnotationType(), anon));
	}

	public Stream<? extends AnnotationValue> readAnnotation(Element element,
			Class<? extends Annotation> clazz) {
		AnnotationValues avs = new AnnotationValues(this.env);
		return findAnnotation(element, clazz).flatMap(avs::get);
	}

	public TypeMirror boxedType(TypeKind kind) {
		switch (kind) {
		case BOOLEAN:
		case BYTE:
		case SHORT:
		case INT:
		case LONG:
		case CHAR:
		case FLOAT:
		case DOUBLE:
			Types t = env.getTypeUtils();
			return t.boxedClass(t.getPrimitiveType(kind)).asType();
		default:
			break;
		}
		throw new IllegalArgumentException();
	}

	public TypeMirror toBoxedType(TypeMirror tm) {
		return tm.accept(new TypeKindVisitor8<TypeMirror, TypeMirror>(tm) {
			@Override
			public TypeMirror visitPrimitive(PrimitiveType t, TypeMirror p) {
				return env.getTypeUtils().boxedClass(t).asType();
			}
		}, tm);
	}

	public TypeVisitor<StringBuilder, StringBuilder> newQNVisitor() {
		return new SimpleTypeVisitor8<StringBuilder, StringBuilder>() {
			@Override
			public StringBuilder visitDeclared(DeclaredType t, StringBuilder p) {
				toTypeElement(t).ifPresent(
						te -> p.append(te.getQualifiedName()));
				return p;
			}
		};
	}
}
