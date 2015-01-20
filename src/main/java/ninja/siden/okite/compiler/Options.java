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

import java.util.Comparator;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Stream;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import ninja.siden.okite.Constants;

/**
 * @author taichi
 */
@SuppressWarnings("unchecked")
public class Options {

	enum Kind {
		debug("false"), prefix(Constants.DEFAULT_PREFIX), suffix(
				Constants.DEFAULT_SUFFIX), casecading("false"),
		// java, javascript(js), schema(json schema?)
		output("java");

		String defaut;

		Kind(String value) {
			this.defaut = value;
		}

		String toOption() {
			return Constants.NAME + "." + name();
		};
	}

	final ProcessingEnvironment env;

	public Options(ProcessingEnvironment env) {
		super();
		this.env = env;
	}

	public String prefix() {
		String s = getOption(Kind.prefix);
		return Objects.toString(s, Constants.DEFAULT_PREFIX);
	}

	public String suffix() {
		String s = getOption(Kind.suffix);
		return Objects.toString(s, Constants.DEFAULT_SUFFIX);
	}

	public boolean casecading() {
		return Boolean.parseBoolean(getOption(Kind.casecading));
	}

	public Stream<String> output() {
		String s = getOption(Kind.output);
		return Stream.of(s.split(","));
	}

	public boolean debug() {
		return Boolean.parseBoolean(getOption(Kind.debug));
	}

	public String version() {
		return debug() ? "@@VERSION@@" : Constants.VERSION;
	}

	public Date now() {
		return debug() ? new Date(0L) : new Date();
	}

	protected String getOption(Kind k) {
		return this.env.getOptions().get(k.toOption());
	}

	static final Comparator<Element> memberComparator = newComparator();

	static Comparator<Element> newComparator() {
		try {
			Class<?> clazz = Options.class.getClassLoader().loadClass(
					"ninja.siden.okite.compiler.internal.ASTComparator");
			return (Comparator<Element>) clazz.newInstance();
		} catch (Exception e) {
			return (l, r) -> 0;
		}
	}

	public Comparator<Element> memberComparator() {
		return memberComparator;
	}
}
