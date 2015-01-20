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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic.Kind;

/**
 * @author taichi
 */
public class Classes {

	ProcessingEnvironment env;

	public Classes(ProcessingEnvironment env) {
		this.env = env;
	}

	public String toSimpleName(Class<?> clazz) {
		// TODO cut some package names. cf. lava.lang and some.
		return toSimpleName(clazz.getCanonicalName());
	}

	public String toSimpleName(String qualifiedName) {
		int pos = qualifiedName.lastIndexOf('.');
		if (pos < 0) {
			return qualifiedName;
		}
		return qualifiedName.substring(pos + 1);
	}

	public <T> Optional<T> newInstance(String className) {
		Optional<Class<T>> opt = loadClass(className);
		return opt.map(cls -> {
			try {
				return cls.newInstance();
			} catch (Throwable e) {
				this.env.getMessager().printMessage(Kind.WARNING,
						e.getMessage());
			}
			return null;
		});
	}

	public <T> Optional<Class<T>> loadClass(String className) {
		ClassLoader loader = findClassLoader();
		try {
			@SuppressWarnings("unchecked")
			Class<T> clazz = (Class<T>) loader.loadClass(className);
			return Optional.of(clazz);
		} catch (Throwable e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			this.env.getMessager().printMessage(Kind.WARNING, sw.toString());
		}
		return Optional.empty();
	}

	public ClassLoader findClassLoader() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader != null) {
			return loader;
		}
		return Classes.class.getClassLoader();
	}
}
