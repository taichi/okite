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

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Types;

import ninja.siden.okite.compiler.internal.BuiltIns;

/**
 * @author taichi
 */
public class Env {

	public ProcessingEnvironment env;

	public Options options;
	public Classes classUtils;
	public AnnotationValues values;
	public Elements elemUtils;
	public Types typeUtils;
	public Filer filer;
	public BuiltIns builtIns;
	public AnnotationRepository repos;

	public Env(ProcessingEnvironment env) {
		this.env = env;
		this.options = new Options(env);
		this.classUtils = new Classes(env);
		this.values = new AnnotationValues(env);
		this.elemUtils = new Elements(env);
		this.typeUtils = this.env.getTypeUtils();
		this.filer = this.env.getFiler();
		this.builtIns = new BuiltIns(env);
		this.repos = new AnnotationRepository(this);
	}

}
