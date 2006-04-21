/**
 *
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.wire.service;

import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.core.wire.MethodHashMap;
import org.apache.tuscany.core.wire.ProxyFactoryFactory;
import org.apache.tuscany.core.wire.SourceInvocationConfiguration;
import org.apache.tuscany.core.wire.SourceWireFactory;
import org.apache.tuscany.core.wire.TargetInvocationConfiguration;
import org.apache.tuscany.core.wire.TargetWireFactory;
import org.apache.tuscany.core.wire.WireSourceConfiguration;
import org.apache.tuscany.core.wire.WireTargetConfiguration;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The default implementation of the <code>WireFactoryService</code> in the runtime
 *
 * @version $$Rev$$ $$Date$$
 */
@Scope("MODULE")
@Service(interfaces = {WireFactoryService.class})
public class DefaultWireFactoryService implements WireFactoryService {

    private MessageFactory messageFactory;

    public DefaultWireFactoryService(){

    }

    public DefaultWireFactoryService(MessageFactory messageFactory,ProxyFactoryFactory proxyFactory){
        this.messageFactory = messageFactory;
        this.proxyFactory = proxyFactory;
    }

    @Autowire
    public void setMessageFactory(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    private ProxyFactoryFactory proxyFactory;

    @Autowire
    public void setProxyFactory(ProxyFactoryFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }

    @Init(eager = true)
    public void init() {
    }

    public SourceWireFactory createSourceFactory(String referenceName, QualifiedName targetName, Class interfaze) {
        SourceWireFactory wireFactory = proxyFactory.createSourceWireFactory();
        Map<Method, SourceInvocationConfiguration> iConfigMap = new HashMap<Method, SourceInvocationConfiguration>();
        Set<Method> javaMethods = JavaIntrospectionHelper.getAllUniqueMethods(interfaze);
        for (Method method : javaMethods) {
            SourceInvocationConfiguration iConfig = new SourceInvocationConfiguration(method);
            iConfigMap.put(method, iConfig);
        }
        WireSourceConfiguration pConfiguration = new WireSourceConfiguration(referenceName, targetName, iConfigMap, interfaze.getClassLoader(),
                messageFactory);
        wireFactory.setBusinessInterface(interfaze);
        wireFactory.setConfiguration(pConfiguration);
        return wireFactory;
    }

    public TargetWireFactory createTargetFactory(QualifiedName targetName, Class interfaze) {
        Map<Method, TargetInvocationConfiguration> iConfigMap = new MethodHashMap<TargetInvocationConfiguration>();
        TargetWireFactory wireFactory = proxyFactory.createTargetWireFactory();
        Set<Method> javaMethods = JavaIntrospectionHelper.getAllUniqueMethods(interfaze);
        for (Method method : javaMethods) {
            TargetInvocationConfiguration iConfig = new TargetInvocationConfiguration(method);
            iConfigMap.put(method, iConfig);
        }
        WireTargetConfiguration wireConfiguration = new WireTargetConfiguration(targetName, iConfigMap, interfaze.getClassLoader(), messageFactory);
        wireFactory.setBusinessInterface(interfaze);
        wireFactory.setConfiguration(wireConfiguration);
        return wireFactory;
    }


}
