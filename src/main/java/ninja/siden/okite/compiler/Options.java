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

import java.util.Date;

import javax.annotation.processing.ProcessingEnvironment;

import ninja.siden.okite.Constants;

/**
 * @author taichi
 */
public class Options {

	final ProcessingEnvironment env;

	public Options(ProcessingEnvironment env) {
		super();
		this.env = env;
	}

	public boolean debug() {
		String debug = env.getOptions().get("DEBUG");
		return Boolean.parseBoolean(debug);
	}

	public String version() {
		return debug() ? "@@VERSION@@" : Constants.VERSION;
	}

	public Date now() {
		return debug() ? new Date(0L) : new Date();
	}
}
