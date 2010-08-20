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

package org.apache.tuscany.sca.implementation.spring.xml;

import java.net.URL;
import java.util.List;

import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.implementation.spring.SpringBeanElement;
import org.apache.tuscany.sca.implementation.spring.SpringSCAPropertyElement;
import org.apache.tuscany.sca.implementation.spring.SpringSCAReferenceElement;
import org.apache.tuscany.sca.implementation.spring.SpringSCAServiceElement;

/**
 * The utility interface to load Spring XML bean definitions into an application context 
 */
public interface SpringXMLBeanDefinitionLoader {
    /**
     * @param resources
     * @param serviceElements
     * @param referenceElements
     * @param propertyElements
     * @param beanElements
     * @param context
     * @return
     */
    Object load(List<URL> resources,
                List<SpringSCAServiceElement> serviceElements,
                List<SpringSCAReferenceElement> referenceElements,
                List<SpringSCAPropertyElement> propertyElements,
                List<SpringBeanElement> beanElements,
                ProcessorContext context);
}
