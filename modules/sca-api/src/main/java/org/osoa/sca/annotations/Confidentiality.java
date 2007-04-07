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
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

import static org.osoa.sca.Constants.SCA_PREFIX;

/**
 * Annotation denoting the intent that service operations require confidentiality.
 * <p/>
 * Applied to the injection site (field, method or constructor parameter) for a reference,
 * it indicates that all invocations through that reference require confidentiality.
 * <p/>
 * Applied to a interface method on a service contract, it indicates that all invocations
 * of that service operation require confidentiality; applied to the type of a service contract,
 * it indicates that all service operations on that interface require confidentiality.
 * <p/>
 * Applied to a method on an implementation class, it indicates that all invocations that
 * are dispatched to that implementation method (through any service) require confidentiality.
 * Applied to a interface implemented by an implementation class, it indicates that all
 * invocations that are dispatched to the implementation method for that interface operation
 * require confidentiality.
 * <p/>
 * Applied to an implementation class, it indicates that all invocations of that implementation
 * and that all invocations made by that implementation require confidentiality.
 *
 * @version $Rev$ $Date$
 */
@Inherited
@Target({TYPE, FIELD, METHOD, PARAMETER})
@Retention(RUNTIME)
@Intent(Confidentiality.CONFIDENTIALITY)
public @interface Confidentiality {
    String CONFIDENTIALITY = SCA_PREFIX + "confidentiality";
    String CONFIDENTIALITY_MESSAGE = CONFIDENTIALITY + ".message";
    String CONFIDENTIALITY_TRANSPORT = CONFIDENTIALITY + ".transport";

    /**
     * List of confidentiality qualifiers (such as "message" or "transport").
     *
     * @return confidentiality qualifiers
     */
    @Qualifier
    String[] value() default "";
}
