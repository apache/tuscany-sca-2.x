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

package org.apache.tuscany.implementation.spring.itests.helloworld;

/**
 * A test case designed to test the implementation of References from a Spring application
 * context, where the references are implicit, through the presence of Bean properties with a 
 * ref attribute which is not satisfied by a Bean within the application context.
 * 
 * The artifacts involved in this test are:
 * 
 * 1) A composite containing a component with a Spring implementation which makes
 * a reference to a second component
 * 2) The composite has a component with a Java POJO implementation which satisfies the reference
 * 3) The <implementation.spring.../> element references an application context that
 * does not use an explicit sca:reference element to identify the reference made by the 
 * Spring application, but relies on an unsatisfied Bean property with a ref attribute.
 * 
 * @author MikeEdwards
 */
public class SpringImplicitReferenceTestCase extends AbstractHelloWorldTestCase {
    // super class does it all getting composite based on this class name
}


