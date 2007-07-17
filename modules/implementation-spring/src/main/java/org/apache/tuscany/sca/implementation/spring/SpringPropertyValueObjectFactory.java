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

package org.apache.tuscany.sca.implementation.spring;

import org.apache.tuscany.sca.implementation.java.context.JavaPropertyValueObjectFactory;
import org.apache.tuscany.sca.databinding.Mediator;

/**
 *
 * Factory class for PropertyValueObjects for Spring implementations
 *
 * 6th May 2007: Chosen a very simple design for this class - since Spring implementations are a form
 * of Java POJO, the simple design chosen for this class is to re-use the PropertyValueObjectFactory
 * implementation from the base implementation-java-runtime package of Tuscany SCA Java, since the
 * same properties are going to be rendered in the same way to simple Tuscany POJOs and to Spring
 * Bean POJOs.  Mike Edwards
 */
public class SpringPropertyValueObjectFactory extends JavaPropertyValueObjectFactory {

	/**
	 * Constructor simply defers to the superclass, along with the complete implementation...
	 */
    public SpringPropertyValueObjectFactory(Mediator mediator) {
        super(mediator);
    } // end constructor JavaPropertyValueObjectFactory(Mediator mediator)

} // end class SpringPropertyValueObjectFactory