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

import ninja.siden.okite.annotation.AnnotateWith;
import ninja.siden.okite.annotation.AnnotateWith.AnnotationTarget;

/**
 * @author taichi
 */
@MetaValidation(with = @AnnotateWith(value = Override.class, target = AnnotationTarget.CONSTRUCTOR, values = "ccc=dddd"))
public class MetaVal {
}
