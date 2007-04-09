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
package org.apache.tuscany.container.spring;

import java.net.URI;
import javax.xml.namespace.QName;

import org.apache.tuscany.spi.builder.WiringException;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.extension.ServiceBindingExtension;
import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.physical.PhysicalOperationDefinition;
import org.apache.tuscany.spi.wire.TargetInvoker;

import org.apache.tuscany.container.spring.mock.TestBeanImpl;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.GenericApplicationContext;

/**
 * @version $$Rev$$ $$Date$$
 */

public final class SpringTestUtils {
    private SpringTestUtils() {
    }

    public static <T> ServiceBinding createService(URI uri, Class<T> serviceInterface)
        throws InvalidServiceContractException, WiringException {
        return new ServiceBindingExtension(uri) {
            public QName getBindingType() {
                return null;
            }

            public TargetInvoker createTargetInvoker(String targetName, Operation operation)
                throws TargetInvokerCreationException {
                return null;
            }

            public TargetInvoker createTargetInvoker(String targetName, PhysicalOperationDefinition operation)
                throws TargetInvokerCreationException {
                return null;
            }
        };
    }


    public static GenericApplicationContext createContext() {
        GenericApplicationContext ctx = new GenericApplicationContext();
        BeanDefinition definition = new RootBeanDefinition(TestBeanImpl.class);
        ctx.registerBeanDefinition("foo", definition);
        return ctx;
    }

}
