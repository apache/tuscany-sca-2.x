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

import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.runtime.RuntimeComponent;

/**
 * An ImplementationActivator adds an SCA implementation type to the Tuscany runtime.
 * 
 * The SCDL XML used for the implementation is derived from the name of the
 * class returned from the getImplementationClass method - the package name and
 * any trailing "Implementation" string is removed, leading upper case characters
 * are converted to lowercase, and the suffix "implementation." is added. For 
 * example if getImplementationClass returns a class named "mypkg.FooImplementation"
 * then the SCDL for the implementation will be <implementation.foo>.
 * 
 * Attributes of the <implementation.foo> SCDL are based on the getters/setters of
 * the Implementation class. So if FooImplementation had getBar/setBar then there
 * would be an attribute name 'bar', for example, <implementation.foo bar="xxx">. 
 * 
 * BindingActivator implementations may use constructor arguments to have 
 * Tuscany ExtensionPointRegistry objects passed in on their constructor.
 * For example:
 * 
 *    public class MyImplementationActivator implements ImplementationActivator {
 *       ServletHost servletHost;
 *       public MyImplementationActivator(ServletHost servletHost) {
 *          this.servletHost = servletHost;
 *       }
 *       ...
 *    }
 *    
 * ImplementationActivator implementations are discovered by the Tuscany runtime
 * using the J2SE jar file extensions for service provider discovery. All
 * that means is packaging the new binding type in a jar which contains a  
 * file META-INF/services/org.apache.tuscany.sca.spi.ImplementationActivator and
 * that file lists the ImplementationActivator implementation class name.
 */
public interface ImplementationActivator<T> {

    Class<T> getImplementationClass();

    InvokerFactory createInvokerFactory(RuntimeComponent rc, ComponentType ct, T implementation);
}
