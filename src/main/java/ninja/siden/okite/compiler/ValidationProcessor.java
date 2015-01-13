package ninja.siden.okite.compiler;

import io.gige.util.ElementFilter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.annotation.Generated;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Completion;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;

import ninja.siden.okite.Constants;
import ninja.siden.okite.Constraint;
import ninja.siden.okite.MessageResolver;
import ninja.siden.okite.ValidationContext;
import ninja.siden.okite.Violation;
import ninja.siden.okite.annotation.Emitter;
import ninja.siden.okite.annotation.Implements;
import ninja.siden.okite.annotation.Validate;
import ninja.siden.okite.compiler.internal.BuiltIns;
import ninja.siden.okite.compiler.internal.ImplementsEmitter;
import ninja.siden.okite.compiler.internal.ValidateEmitter;
import ninja.siden.okite.internal.BaseValidator;
import ninja.siden.okite.internal.DefaultValidationContext;

/**
 * @author taichi
 */
public class ValidationProcessor extends AbstractProcessor {

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return new HashSet<>(Arrays.asList("*"));
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.RELEASE_8;
	}

	Options options;
	Classes classUtils;
	Elements elemUtils;
	Types typeUtils;
	BuiltIns builtIns;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		this.options = new Options(this.processingEnv);
		this.classUtils = new Classes(this.processingEnv);
		this.elemUtils = new Elements(this.processingEnv);
		this.typeUtils = this.processingEnv.getTypeUtils();
		this.builtIns = new BuiltIns(this.processingEnv);
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			javax.annotation.processing.RoundEnvironment originalRE) {
		if (originalRE.processingOver()) {
			return false;
		}
		RoundEnvironment roundEnv = new RoundEnvironment(originalRE);

		Stream<TypeElement> targets = findTargets(roundEnv);
		targets.forEach(te -> process(roundEnv, te));

		return false;
	}

	void process(RoundEnvironment roundEnv, TypeElement te) {
		String pkg = elemUtils.getPackageName(te);
		String cls = te.getQualifiedName().toString();
		String validatorName = cls + "$$Validator";
		String simpleName = Classes.toSimpleName(validatorName);

		Filer filer = processingEnv.getFiler();
		try {
			JavaFileObject jfo = filer.createSourceFile(validatorName, te);
			try (PrintWriter pw = new PrintWriter(new BufferedWriter(
					jfo.openWriter()))) {
				pw.format("package %s;%n", pkg);
				pw.println();
				pw.println("import java.util.*;");
				pw.println("import ninja.siden.okite.*;");
				pw.println("import ninja.siden.okite.internal.*;");
				printGenerated(pw);
				pw.format("public class %s extends %s<%s> {%n", simpleName,
						BaseValidator.class.getSimpleName(), cls);

				printConstructor(roundEnv, te, simpleName, pw);
				printField(roundEnv, te, pw);
				printValueMethods(roundEnv, te, pw);
				printValidate(roundEnv, te, pw);

				pw.println("}// class");
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	void printValidate(RoundEnvironment roundEnv, TypeElement te, PrintWriter pw) {
		Stream<ExecutableElement> validates = ElementFilter.methodsIn(te)
				.filter(validateMethods());
		validates.forEach(ee -> {
			pw.printf("private void _%s(%s resolver) {%n", ee.getSimpleName(),
					MessageResolver.class.getSimpleName());

			Stream<? extends AnnotationMirror> mirrors = elemUtils
					.findAnnotation(ee, Validate.class);
			mirrors.forEach(am -> {
				ValidateEmitter emitter = new ValidateEmitter();
				emitter.emit(this.processingEnv, pw, am, ee);
			});

			pw.println("}");
		});
	}

	void printField(RoundEnvironment roundEnv, TypeElement te, PrintWriter pw) {
		Stream<VariableElement> fields = ElementFilter.fieldsIn(te).filter(
				constraintFilter(roundEnv));
		fields.forEach(ve -> {
			pw.printf("private void _%s(%s resolver) {%n", ve.getSimpleName(),
					MessageResolver.class.getSimpleName());
			String type = elemUtils.toBoxedClassName(ve.asType());

			pw.printf("%s<%s<%s>> constraints = new %s<>();%n",
					SortedSet.class.getSimpleName(),
					Constraint.class.getSimpleName(), type,
					TreeSet.class.getSimpleName());

			List<? extends AnnotationMirror> mirrors = ve
					.getAnnotationMirrors();
			mirrors.forEach(am -> {
				Optional<ConstraintEmitter> opt = toImpl(am);
				opt.ifPresent(ct -> {
					pw.println("{");
					ct.emit(this.processingEnv, pw, am, ve);
					pw.println("constraints.add(c);");
					pw.println("}//local variable");
				});
			});
			pw.printf(
					"validations.add(v -> validate(v.%s, constraints,new %s(resolver, \"%s\")));",
					ve.getSimpleName(),
					DefaultValidationContext.class.getSimpleName(),
					ve.getSimpleName());

			pw.println("}// method");
		});
	}

	void printValueMethods(RoundEnvironment roundEnv, TypeElement te,
			PrintWriter pw) {
		Stream<ExecutableElement> values = ElementFilter.methodsIn(te).filter(
				valueMethods().and(constraintFilter(roundEnv)));
		values.forEach(ee -> {
			pw.printf("private void _%s(%s resolver) {%n", ee.getSimpleName(),
					MessageResolver.class.getSimpleName());
			String type = elemUtils.toBoxedClassName(ee.getReturnType());

			pw.printf("%s<%s<%s>> constraints = new %s<>();%n",
					SortedSet.class.getSimpleName(),
					Constraint.class.getSimpleName(), type,
					TreeSet.class.getSimpleName());

			List<? extends AnnotationMirror> mirrors = ee
					.getAnnotationMirrors();
			mirrors.forEach(am -> {
				Optional<ConstraintEmitter> opt = toImpl(am);
				opt.ifPresent(ct -> {
					pw.println("{");
					ct.emit(this.processingEnv, pw, am, ee);
					pw.println("constraints.add(c);");
					pw.println("}//local variable");
				});
			});
			pw.printf(
					"validations.add(v -> validate(v.%s(), constraints,new %s(resolver, \"%s\")));",
					ee.getSimpleName(),
					DefaultValidationContext.class.getSimpleName(),
					ee.getSimpleName());

			pw.println("}// method");
		});
	}

	Optional<ConstraintEmitter> toImpl(AnnotationMirror constAnon) {
		DeclaredType type = constAnon.getAnnotationType();
		TypeElement te = elemUtils.toTypeElement(type).get();

		Optional<ConstraintEmitter> impOpt = elemUtils
				.readAnnotation(te, Implements.class)
				.map(AnnotationValues::readType).filter(Optional::isPresent)
				.map(Optional::get).findAny()
				.map(tm -> new ImplementsEmitter(elemUtils.getClassName(tm)));
		if (impOpt.isPresent()) {
			return impOpt;
		}

		Optional<String> emOpt = elemUtils.readAnnotation(te, Emitter.class)
				.map(AnnotationValues::readString).filter(Optional::isPresent)
				.map(Optional::get).findAny();
		return emOpt.flatMap(classUtils::newInstance);
	}

	void printConstructor(RoundEnvironment roundEnv, TypeElement te,
			String simpleName, PrintWriter w) {
		w.printf("public %s(%s resolver) {%n", simpleName,
				MessageResolver.class.getSimpleName());
		memberNames(roundEnv, te).forEach(n -> {
			w.printf("_%s(resolver);%n", n);
		});
		w.println("}// constructor");
	}

	Stream<Name> memberNames(RoundEnvironment roundEnv, TypeElement te) {
		// TODO @Order によるメンバのソート
		Stream<VariableElement> fields = ElementFilter.fieldsIn(te).filter(
				constraintFilter(roundEnv));
		Stream<ExecutableElement> values = ElementFilter.methodsIn(te).filter(
				valueMethods().and(constraintFilter(roundEnv)));
		Stream<ExecutableElement> validates = ElementFilter.methodsIn(te)
				.filter(validateMethods());
		Stream<ExecutableElement> methods = Stream.concat(values, validates);
		Stream<Element> members = Stream.concat(fields, methods);
		return members.map(e -> e.getSimpleName());
	}

	Predicate<ExecutableElement> valueMethods() {
		// TODO need debug information?
		return e -> e.getParameters().size() < 1
				&& e.getReturnType().getKind().equals(TypeKind.VOID) == false;
	}

	<T extends Element> Predicate<T> constraintFilter(RoundEnvironment roundEnv) {
		return e -> {
			List<? extends AnnotationMirror> mirrors = e.getAnnotationMirrors();
			return mirrors.stream().anyMatch(am -> {
				return constraints(roundEnv).anyMatch(te -> {
					TypeMirror annon = am.getAnnotationType();
					return typeUtils.isSameType(annon, te.asType());
				});
			});
		};
	}

	Predicate<ExecutableElement> validateMethods() {
		// TODO need debug information?
		return argsFilter().and(returnTypeFilter()).and(
				e -> e.getAnnotation(Validate.class) != null);
	}

	Predicate<ExecutableElement> argsFilter() {
		return e -> {
			List<? extends VariableElement> params = e.getParameters();
			return params.size() == 1
					&& params.get(0).asType().toString()
							.equals(ValidationContext.class.getCanonicalName());
		};
	};

	Predicate<ExecutableElement> returnTypeFilter() {
		return e -> {
			TypeMirror tm = e.getReturnType();
			if (tm.getKind().equals(TypeKind.VOID)) {
				return false;
			}
			String type = tm.toString();
			if (Violation[].class.getCanonicalName().equals(type)) {
				return true;
			}
			if (type.endsWith("<" + Violation.class.getCanonicalName() + ">") == false) {
				return false;
			}
			return Stream
					.of(Collection.class, Stream.class)
					.map(elemUtils::getTypeElement)
					.filter(Optional::isPresent)
					.map(Optional::get)
					.anyMatch(
							te -> {
								return typeUtils.isAssignable(
										typeUtils.erasure(tm), te.asType());
							});
		};
	}

	Stream<TypeElement> findTargets(RoundEnvironment roundEnv) {
		// * @Validation, @MapValidationの付加されているクラスは処理対象となる
		// * @Validation, @MapValidationの付加されているアノテーションを検索する
		// ** そこで見つかったアノテーションが付加されているクラスは処理対象となる
		// * 標準のConstraintアノテーションが付加されているフィールドを持っているクラスは処理対象となる
		// * @Implementsもしくは@Selector の付加されているアノテーションを検索する
		// ** そこで見つかったアノテーションが付加されているフィールドを持っているクラスは処理対象となる
		Stream<TypeElement> typeAnnotations = combine(roundEnv,
				builtIns.validation(), builtIns.validation());
		Stream<TypeElement> first = typeAnnotations
				.flatMap(roundEnv::getElementsAnnotatedWith)
				.filter(ElementFilter.of(ElementKind.CLASS))
				.map(TypeElement.class::cast);

		Stream<TypeElement> second = constraints(roundEnv)
				.flatMap(roundEnv::getElementsAnnotatedWith)
				.map(Element::getEnclosingElement).map(this::toType)
				.filter(Optional::isPresent).map(Optional::get);

		// TODO コンパイル時間を短縮するために、ここだけ切り離してシリアライズできるようにする。
		return Stream.concat(first, second).distinct();
	}

	Stream<TypeElement> constraints(RoundEnvironment roundEnv) {
		return combine(roundEnv, builtIns.metaConstraint(),
				builtIns.constraint());
	}

	Stream<TypeElement> combine(RoundEnvironment roundEnv,
			Stream<Class<? extends Annotation>> meta,
			Stream<Class<? extends Annotation>> builtInClass) {
		Stream<TypeElement> additionals = meta
				.flatMap(roundEnv::getElementsAnnotatedWith)
				.filter(ElementFilter.of(ElementKind.ANNOTATION_TYPE))
				.map(TypeElement.class::cast);

		Stream<TypeElement> builtIn = builtInClass.map(
				clazz -> elemUtils.getTypeElement(clazz)).map(Optional::get);

		return Stream.concat(builtIn, additionals);
	}

	Optional<TypeElement> toType(Element annotated) {
		switch (annotated.getKind()) {
		case CLASS:
			return Optional.of(TypeElement.class.cast(annotated));
		case METHOD:
		case FIELD:
			return Optional.of(TypeElement.class.cast(annotated
					.getEnclosingElement()));
		default:
			return Optional.empty();
		}
	}

	protected void printGenerated(PrintWriter pw) {
		pw.printf(
				"@%s(value = { \"%s\", \"%s\" }, date = \"%tFT%<tT.%<tL%<tz\")%n",
				Generated.class.getName(), Constants.NAME, options.version(),
				options.now());
	}

	@Override
	public Iterable<? extends Completion> getCompletions(Element element,
			AnnotationMirror annotation, ExecutableElement member,
			String userText) {
		return super.getCompletions(element, annotation, member, userText);
	}

}
