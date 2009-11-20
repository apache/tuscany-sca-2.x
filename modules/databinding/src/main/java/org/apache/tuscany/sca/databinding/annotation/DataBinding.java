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
package org.apache.tuscany.sca.databinding.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Used to demarcate the mapping style for an interface or operation
 *
 * @version $Rev$ $Date$
 */
// FIXME: [rfeng] We should consider to use javax.jws.soap.SOAPBinding
@Target({TYPE, METHOD})
@Retention(RUNTIME)
public @interface DataBinding {

    /**
     * Indicate the effective databinding that controls the WSDL/Java mapping of the 
     * interface/operation
     * 
     * @return the data binding with the MIME media type syntax
     */
    String value();
    
    /**
     * Indicate if the operation is mapped using WRAPPED or BARE style. Originated from
     * javax.jws.soap.SOAPBinding.ParameterStyle: Determines whether method parameters 
     * represent the entire message body, or whether the parameters are elements wrapped 
     * inside a top-level element named after the operation
     * 
     * @return true if the parameter style is WRAPPED, false if BARE
     */
    boolean wrapped() default false;

}
