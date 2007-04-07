/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.osoa.sca.annotations;

import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * Annotation used to indicate a scoped service.
 * <p/>
 * The spec refers to but does not describe an eager() attribute; this is an error in the draft.
 *
 * @version $Rev$ $Date$
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Scope {
    /**
     * The name of the scope. Values currently defined by the specification are:
     * <ul>
     * <li>STATELESS (default)</li>
     * <li>REQUEST</li>
     * <li>CONVERSATION</li>
     * <li>COMPOSITE</li>
     * </ul>
     *
     * @return the name of the scope
     */
    String value() default "STATELESS";
}
