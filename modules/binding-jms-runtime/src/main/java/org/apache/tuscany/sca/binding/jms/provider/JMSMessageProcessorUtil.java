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

package org.apache.tuscany.sca.binding.jms.provider;

import java.lang.reflect.Constructor;

import org.apache.tuscany.sca.binding.jms.impl.JMSBinding;
import org.apache.tuscany.sca.binding.jms.impl.JMSBindingException;

/**
 * Utility methods to load JMS message processors.
 * 
 * @version $Rev: $ $Date: $
 */
public class JMSMessageProcessorUtil {

    /**
     * Used to create instances of the JMSResourceFactory and RequestMessageProcessor and ResponseMessageProcessor from 
     * string based class name provided in the configuration
     * 
     * @param cl ClassLoader
     * @param className the string based class name to load and instantiate
     * @return the new object
     */
    private static Object instantiate(ClassLoader cl, String className, JMSBinding binding) {
        Object instance;
        if (cl == null) {
            cl = binding.getClass().getClassLoader();
        }

        try {
            Class<?> clazz;

            try {
                clazz = cl.loadClass(className);
            } catch (ClassNotFoundException e) {
                clazz = binding.getClass().getClassLoader().loadClass(className);
            }

            Constructor<?> constructor = clazz.getDeclaredConstructor(new Class[] {JMSBinding.class});
            instance = constructor.newInstance(binding);

        } catch (Throwable e) {
            throw new JMSBindingException("Exception instantiating OperationAndDataBinding class", e);
        }

        return instance;
    }

    public static JMSMessageProcessor getRequestMessageProcessor(JMSBinding binding) {
        return (JMSMessageProcessor)instantiate(null, binding.getRequestMessageProcessorName(), binding);
    }

    public static JMSMessageProcessor getResponseMessageProcessor(JMSBinding binding) {
        return (JMSMessageProcessor)instantiate(null, binding.getResponseMessageProcessorName(), binding);
    }

}
