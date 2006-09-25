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

package org.apache.tuscany.core.databinding.impl;

import org.apache.tuscany.spi.databinding.extension.DataBindingExtension;
import org.apache.tuscany.spi.loader.MissingResourceException;
import org.osoa.sca.annotations.Property;

/**
 * Simple databinding represented by a base java type. A SCDL property className is used to customize
 * this component. 
 * <p>
 * The following illustrates how a simple data binding can be registered as a SCA component.<p>
 * &lt;component name="databinding.MyDataBinding"&gt;<br>
 * &nbsp;&nbsp;&lt;system:implementation.java class="org.apache.tuscany.databinding.impl.SimpleDataBinding"/&gt;<br>
 * &nbsp;&nbsp;&lt;property name="className"&gt;my.Type&lt;/property&gt;<br>
 * &lt/component&gt;
 */
public class SimpleDataBinding extends DataBindingExtension {

    public SimpleDataBinding(@Property(name = "className") String className) throws MissingResourceException {
        super(resolve(className));
    }

    private static Class<?> resolve(String className) throws MissingResourceException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            return Class.forName(className, false, classLoader);
        } catch (ClassNotFoundException e) {
            classLoader = SimpleDataBinding.class.getClassLoader();
            try {
                return Class.forName(className, false, classLoader);
            } catch (ClassNotFoundException e1) {
                MissingResourceException mre = new MissingResourceException(className, e1);
                throw mre;
            }
        }
    }

}
