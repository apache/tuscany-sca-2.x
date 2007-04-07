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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * Annotation used to indicate a constructor parameter, field or method that is
 * used to inject a configuration property value.
 *
 * @version $Rev$ $Date$
 */
@Target({METHOD, FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface Property {
    /**
     * The name of the property. If not specified then the name will be derived
     * from the annotated field or method.
     *
     * @return the name of the property
     */
    String name() default "";

    /**
     * Indicates whether a value for the property must be provided.
     *
     * @return true if a value must be provided
     */
    boolean required() default false;
}
