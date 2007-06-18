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

package org.apache.tuscany.sca.spi;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * A BindingActivator adds an SCA binding type to the Tuscany runtime.
 * 
 * The SCDL XML used for the binding is derived from the name of the
 * class returned from the getBindingClass method - the package name and
 * any trailing "Binding" string is removed, leading upper case characters
 * are converted to lowercase, and the suffix "binding." is added.
 * For example if getBindingClass returns a class named "mypkg.FooBinding"
 * then the SCDL for the binding will be <binding.foo>.
 * 
 * Attributes of the <binding.foo> SCDL are based on the getters/setters of
 * the binding class. So if FooBinding had getBar/setBar then there
 * would be an attribute name 'bar', for example, <binding.foo bar="xxx">. 
 * 
 * BindingActivator implementations may use constructor arguments to have 
 * Tuscany ExtensionPointRegistry objects passed in on their constructor.
 * For example:
 * 
 *    public class MyBindingActivator implements BindingActivator {
 *       ServletHost servletHost;
 *       public MyBindingActivator(ServletHost servletHost) {
 *          this.servletHost = servletHost;
 *       }
 *       ...
 *    }
 *    
 * BindingActivator implementations are discovered by the Tuscany runtime
 * using the J2SE jar file extensions for service provider discovery. All
 * that means is packaging the new binding type in a jar which contains a  
 * file META-INF/services/org.apache.tuscany.sca.spi.BindingActivator and
 * that file lists the BindingActivator implementation class name.
 */
public interface BindingActivator<B> {

    Class<B> getBindingClass();

    InvokerFactory createInvokerFactory(RuntimeComponent rc, RuntimeComponentReference rcr, Binding b, B pojoBinding);
    
    ComponentLifecycle createService(RuntimeComponent rc, RuntimeComponentService rcs, Binding b, B pojoBinding);
    
}
