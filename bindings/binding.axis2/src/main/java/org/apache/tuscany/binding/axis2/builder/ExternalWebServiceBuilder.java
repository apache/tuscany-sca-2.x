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
package org.apache.tuscany.binding.axis2.builder;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.AxisService;
import org.apache.tuscany.binding.axis2.assembly.WebServiceBinding;
import org.apache.tuscany.binding.axis2.config.ExternalWebServiceContextFactory;
import org.apache.tuscany.binding.axis2.handler.ExternalWebServiceClient;
import org.apache.tuscany.binding.axis2.handler.WebServicePortMetaData;
import org.apache.tuscany.binding.axis2.util.TuscanyAxisConfigurator;
import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.ContextFactoryBuilder;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.injection.SingletonObjectFactory;
import org.apache.tuscany.core.invocation.InvocationConfiguration;
import org.apache.tuscany.core.invocation.MethodHashMap;
import org.apache.tuscany.core.invocation.WireConfiguration;
import org.apache.tuscany.core.invocation.ProxyFactory;
import org.apache.tuscany.core.invocation.impl.InvokerInterceptor;
import org.apache.tuscany.core.invocation.ProxyFactoryFactory;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.model.assembly.AssemblyObject;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.ServiceContract;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;

import commonj.sdo.helper.TypeHelper;

/**
 * Creates a <code>ContextFactoryBuilder</code> for an external service configured with the {@link WebServiceBinding}
 * 
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class ExternalWebServiceBuilder implements ContextFactoryBuilder {

    private RuntimeContext runtimeContext;

    private ProxyFactoryFactory proxyFactoryFactory;

    private MessageFactory messageFactory;

    private ContextFactoryBuilder policyBuilder;

    public ExternalWebServiceBuilder() {
    }

    @Init(eager = true)
    public void init() {
        runtimeContext.addBuilder(this);
    }

    /**
     * @param runtimeContext The runtimeContext to set.
     */
    @Autowire
    public void setRuntimeContext(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    /**
     * Sets the factory used to construct proxies implmementing the business interface required by a reference
     */
    @Autowire
    public void setProxyFactoryFactory(ProxyFactoryFactory factory) {
        this.proxyFactoryFactory = factory;
    }

    /**
     * Sets the factory used to construct invocation messages
     * 
     * @param msgFactory
     */
    @Autowire
    public void setMessageFactory(MessageFactory msgFactory) {
        this.messageFactory = msgFactory;
    }

    /**
     * Sets a builder responsible for creating source-side and target-side invocation chains for a reference. The
     * reference builder may be hierarchical, containing other child reference builders that operate on specific
     * metadata used to construct and invocation chain.
     * 
     * @see org.apache.tuscany.core.builder.impl.HierarchicalBuilder
     */
    public void setPolicyBuilder(ContextFactoryBuilder builder) {
        policyBuilder = builder;
    }

    public void build(AssemblyObject object) throws BuilderException {
        if (!(object instanceof ExternalService)) {
            return;
        }
        ExternalService externalService = (ExternalService) object;
        if (externalService.getBindings().size() < 1 || !(externalService.getBindings().get(0) instanceof WebServiceBinding)) {
            return;
        }

        ExternalWebServiceClient externalWebServiceClient = createExternalWebServiceClient(externalService);

        ExternalWebServiceContextFactory config = new ExternalWebServiceContextFactory(externalService.getName(), new SingletonObjectFactory<ExternalWebServiceClient>(externalWebServiceClient));

        ConfiguredService configuredService = externalService.getConfiguredService();
        Service service = configuredService.getPort();
        ServiceContract serviceContract = service.getServiceContract();
        Map<Method, InvocationConfiguration> iConfigMap = new MethodHashMap();
        ProxyFactory proxyFactory = proxyFactoryFactory.createProxyFactory();
        Set<Method> javaMethods = JavaIntrospectionHelper.getAllUniqueMethods(serviceContract.getInterface());
        for (Method method : javaMethods) {
            InvocationConfiguration iConfig = new InvocationConfiguration(method);
            iConfigMap.put(method, iConfig);
        }
        QualifiedName qName = new QualifiedName(externalService.getName() + "/" + service.getName());
        WireConfiguration pConfiguration = new WireConfiguration(qName, iConfigMap, serviceContract.getInterface().getClassLoader(), messageFactory);
        proxyFactory.setBusinessInterface(serviceContract.getInterface());
        proxyFactory.setProxyConfiguration(pConfiguration);
        config.addTargetProxyFactory(service.getName(), proxyFactory);
        configuredService.setProxyFactory(proxyFactory);
        if (policyBuilder != null) {
            // invoke the reference builder to handle additional policy metadata
            policyBuilder.build(configuredService);
        }
        // add tail interceptor
        for (InvocationConfiguration iConfig : (Collection<InvocationConfiguration>) iConfigMap.values()) {
            iConfig.addTargetInterceptor(new InvokerInterceptor());
        }

        externalService.setContextFactory(config);
    }

    /**
     * Create an ExternalWebServiceClient for the WebServiceBinding
     */
    protected ExternalWebServiceClient createExternalWebServiceClient(ExternalService externalService) {
        // TODO: Review should there be a single global Axis ConfigurationContext
        TuscanyAxisConfigurator tuscanyAxisConfigurator = new TuscanyAxisConfigurator(null, null);
        ConfigurationContext configurationContext = tuscanyAxisConfigurator.getConfigurationContext();

        WebServiceBinding wsBinding = (WebServiceBinding) externalService.getBindings().get(0);

        WebServicePortMetaData wsPortMetaData = new WebServicePortMetaData(wsBinding.getWSDLDefinition(), wsBinding.getWSDLPort(),
                wsBinding.getURI(), false);
        QName serviceQName = wsPortMetaData.getServiceName();
        String portName = wsPortMetaData.getPortName().getLocalPart();

        AxisService axisService;
        try {
            axisService = AxisService.createClientSideAxisService(wsBinding.getWSDLDefinition(), serviceQName, portName, new Options());
        } catch (AxisFault e) {
            BuilderConfigException bce = new BuilderConfigException("AxisFault creating external service", e);
            bce.addContextName(externalService.getName());
            throw bce;
        }

        TypeHelper typeHelper = externalService.getComposite().getAssemblyContext().getTypeHelper();

        ExternalWebServiceClient externalWebServiceClient = new ExternalWebServiceClient(configurationContext, axisService, wsPortMetaData,
                typeHelper);

        return externalWebServiceClient;
    }

}
