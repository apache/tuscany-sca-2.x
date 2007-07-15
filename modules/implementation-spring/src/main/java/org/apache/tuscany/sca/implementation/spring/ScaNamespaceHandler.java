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
package org.apache.tuscany.sca.implementation.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Handler for the &lt;sca:&gt; namespace in an application context
 *
 * @version $Rev: 511195 $ $Date: 2007-02-24 02:29:46 +0000 (Sat, 24 Feb 2007) $
 */
public class ScaNamespaceHandler extends NamespaceHandlerSupport {

    public ScaNamespaceHandler() {
        init();
    }

    public final void init() {
        registerBeanDefinitionParser("reference", new ScaReferenceBeanDefinitionParser());
        registerBeanDefinitionParser("service", new ScaServiceBeanDefinitionParser());
        registerBeanDefinitionParser("property", new ScaPropertyBeanDefinitionParser());
    }

}
