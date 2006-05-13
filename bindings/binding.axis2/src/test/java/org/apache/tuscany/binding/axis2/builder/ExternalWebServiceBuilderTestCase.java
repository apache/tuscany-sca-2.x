/**
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.binding.axis2.builder;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.tuscany.binding.axis2.assembly.WebServiceBinding;
import org.apache.tuscany.binding.axis2.externalservice.Axis2OperationInvoker;
import org.apache.tuscany.binding.axis2.externalservice.Axis2ServiceInvoker;
import org.apache.tuscany.binding.axis2.util.WebServicePortMetaData;
import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.common.resource.impl.ResourceLoaderImpl;
import org.apache.tuscany.core.extension.ExternalServiceContextFactory;
import org.apache.tuscany.model.assembly.AssemblyContext;
import org.apache.tuscany.model.assembly.AssemblyInitializationException;
import org.apache.tuscany.model.assembly.AssemblyVisitor;
import org.apache.tuscany.model.assembly.Binding;
import org.apache.tuscany.model.assembly.Composite;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.OverrideOption;
import org.apache.tuscany.model.assembly.Part;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.ServiceContract;

import commonj.sdo.helper.TypeHelper;

public class ExternalWebServiceBuilderTestCase extends TestCase {

    private Definition definition;

    private static final QName SERVICE_NAME = new QName("http://helloworldaxis.samples.tuscany.apache.org", "HelloWorldServiceImplService");

    private static final String PORTNAME = "helloworld";

    private static final QName GET_GREETINGS_QN = new QName(SERVICE_NAME.getNamespaceURI(), "getGreetings");

    public void testCreateOperationInvokers() {
        ExternalWebServiceBuilder builder = new ExternalWebServiceBuilder();

        Port port = definition.getService(SERVICE_NAME).getPort(PORTNAME);
        WebServicePortMetaData wspmd = new WebServicePortMetaData(definition, port, null, false);

        Map<String, Axis2OperationInvoker> invokers = builder.createOperationInvokers(Foo.class, null, getClass().getClassLoader(),wspmd);
        assertNotNull(invokers);
        assertEquals(1, invokers.size());

        Axis2OperationInvoker opInvoker = invokers.get(GET_GREETINGS_QN.getLocalPart());
        assertNotNull(opInvoker);
        assertEquals(GET_GREETINGS_QN, opInvoker.getWSDLOperationName());
    }

    public void testCreateExternalServiceContextFactory() {
        ExternalWebServiceBuilder builder = new ExternalWebServiceBuilder();
        ExternalService es = createMockExternalService();
        ExternalServiceContextFactory cf = builder.createExternalServiceContextFactory(es);
        assertNotNull(cf);
        Axis2ServiceInvoker si = (Axis2ServiceInvoker) cf.createContext().getHandler();
        assertNotNull(si);
    }

    protected void setUp() throws Exception {
        super.setUp();
        WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
        reader.setFeature("javax.wsdl.verbose", false);
        URL url = getClass().getResource("helloworld.wsdl");
        this.definition = reader.readWSDL(url.toString());
    }

    interface Foo {
        public void getGreetings();
    };

    private ExternalService createMockExternalService() {
        ExternalService es = new ExternalService() {

            public List<Binding> getBindings() {
                return Arrays.asList(new Binding[] { createMockBinding() });
            }

            public OverrideOption getOverrideOption() {

                return null;
            }

            public void setOverrideOption(OverrideOption value) {

            }

            public ConfiguredService getConfiguredService() {
                return new ConfiguredService() {

                    public String getName() {

                        return null;
                    }

                    public void setName(String name) {

                    }

                    public Service getPort() {
                        return new Service() {

                            public ServiceContract getServiceContract() {
                                return new ServiceContract() {

                                    public Class getInterface() {
                                        return Foo.class;
                                    }

                                    public void setInterface(Class value) {

                                    }

                                    public Class getCallbackInterface() {

                                        return null;
                                    }

                                    public void setCallbackInterface(Class value) {

                                    }

                                    public Scope getScope() {

                                        return null;
                                    }

                                    public void setScope(Scope scope) {

                                    }

                                    public List<Object> getExtensibilityElements() {

                                        return null;
                                    }

                                    public List<Object> getExtensibilityAttributes() {

                                        return null;
                                    }

                                    public void initialize(AssemblyContext modelContext) throws AssemblyInitializationException {

                                    }

                                    public void freeze() {

                                    }

                                    public boolean accept(AssemblyVisitor visitor) {

                                        return false;
                                    }
                                };
                            }

                            public void setServiceContract(ServiceContract contract) {

                            }

                            public String getName() {

                                return null;
                            }

                            public void setName(String name) {

                            }

                            public void initialize(AssemblyContext modelContext) throws AssemblyInitializationException {

                            }

                            public void freeze() {

                            }

                            public boolean accept(AssemblyVisitor visitor) {

                                return false;
                            }
                        };
                    }

                    public void setPort(Service port) {

                    }

                    public Part getPart() {

                        return null;
                    }

                    public void setPart(Part part) {

                    }

                    public void initialize(AssemblyContext modelContext) throws AssemblyInitializationException {

                    }

                    public void freeze() {

                    }

                    public boolean accept(AssemblyVisitor visitor) {

                        return false;
                    }

                    public void setProxyFactory(Object proxyFactory) {

                    }

                    public Object getProxyFactory() {

                        return null;
                    }
                };
            }

            public void setConfiguredService(ConfiguredService configuredService) {

            }

            public String getName() {

                return "myService";
            }

            public void setName(String value) {

            }

            public Composite getComposite() {

                return null;
            }

            public void setComposite(Composite composite) {

            }

            public List<Object> getExtensibilityElements() {

                return null;
            }

            public List<Object> getExtensibilityAttributes() {

                return null;
            }

            public void initialize(AssemblyContext modelContext) throws AssemblyInitializationException {

            }

            public void freeze() {

            }

            public boolean accept(AssemblyVisitor visitor) {

                return false;
            }

            public void setContextFactory(Object contextFactory) {

            }

            public Object getContextFactory() {

                return null;
            }
        };
        return es;
    }

    private Binding createMockBinding() {
        WebServiceBinding b = new WebServiceBinding() {

            public void setPortURI(String portURI) {

            }

            public Port getWSDLPort() {
                return definition.getService(SERVICE_NAME).getPort(PORTNAME);
            }

            public Definition getWSDLDefinition() {
                return definition;
            }

            public void setWSDLPort(Port value) {

            }

            public void setWSDLDefinition(Definition definition) {

            }

            public TypeHelper getTypeHelper() {

                return null;
            }

            public void setTypeHelper(TypeHelper typeHelper) {

            }

            public ResourceLoader getResourceLoader() {
                return new ResourceLoaderImpl(Thread.currentThread().getContextClassLoader());
            }

            public void setResourceLoader(ResourceLoader resourceLoader) {

            }

            public String getURI() {

                return null;
            }

            public void setURI(String value) {

            }

            public void initialize(AssemblyContext modelContext) throws AssemblyInitializationException {

            }

            public void freeze() {

            }

            public boolean accept(AssemblyVisitor visitor) {

                return false;
            }

            public void setWebAppName(String webAppName) {
            }

            public String getWebAppName() {
                return null;
            }

        };
        return b;
    }

}
