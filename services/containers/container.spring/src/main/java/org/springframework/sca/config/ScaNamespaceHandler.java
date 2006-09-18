/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Created on 10-Apr-2006 by Adrian Colyer
 */
package org.springframework.sca.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Handler for the &lt;sca:&gt; namespace. Handles: <ul> <li>&lt;sca:composite module="xxxxx"/&gt;</li>
 * <li>&lt;sca:reference name="xxx" type="yyy" default-service="zzz"/&gt;</li> <li>&lt;sca:property name="xxx"
 * type="yyy"/&gt;</li> <li>&lt;sca:service name="xxx" type="yyyy" target="zzz"/&gt; </ul>
 *
 * @author Adrian Colyer
 * @since 2.0
 */
public class ScaNamespaceHandler extends NamespaceHandlerSupport {

    public ScaNamespaceHandler() {
        // FIXME JFM
        init();
    }

    public final void init() {
        registerBeanDefinitionParser("composite", new ScaCompositeBeanDefinitionParser());
        registerBeanDefinitionParser("reference", new ScaReferenceBeanDefinitionParser());
        registerBeanDefinitionParser("property", new ScaPropertyBeanDefinitionParser());
        registerBeanDefinitionParser("service", new ScaServiceBeanDefinitionParser());
    }

}
