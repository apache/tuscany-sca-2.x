/**
 *
 * Copyright 2006 The Apache Software Foundation
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
package org.apache.tuscany.binding.axis2;

import static org.osoa.sca.Version.XML_NAMESPACE_1_0;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.services.wsdl.WSDLDefinitionRegistry;
import org.osoa.sca.annotations.Scope;
import org.xml.sax.InputSource;

/**
 * Parses a <code>WebServiceBinding</code> entry in an assembly XML file
 * 
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class WebServiceBindingLoader extends LoaderExtension<WebServiceBinding> {
    public static final QName BINDING_WS = new QName(XML_NAMESPACE_1_0, "binding.ws");

    protected WSDLDefinitionRegistry wsdlRegistry;

    private String wsdlLocation;

    private String endpointAttribute;

 
    public WebServiceBindingLoader(@Autowire
    LoaderRegistry registry, @Autowire
    WSDLDefinitionRegistry wsdlRegistry) {
        super(registry);
        this.wsdlRegistry = wsdlRegistry;
    }

    public QName getXMLType() {
        return BINDING_WS;
    }

    public WebServiceBinding load(CompositeComponent parent, XMLStreamReader reader,
            DeploymentContext deploymentContext) throws XMLStreamException,
            LoaderException {
        // not sure what uri was here ? String uri = reader.getAttributeValue(null, "uri");
        String uri = null;
        endpointAttribute = reader.getAttributeValue(null, "endpoint");
        wsdlLocation = reader.getAttributeValue(null, "location");
        try {
            return createBinding(uri, endpointAttribute);
        } catch (Exception e) {

            throw new LoaderException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private WebServiceBinding createBinding(String port, String portURI) throws WSDLException, IOException {
        List<Definition> definitions = null;
        // FIXME wsdlRegistry.getDefinitionsForNamespace(portNamespace,
        // resourceLoader);
        // Get the WSDL port namespace and name
        if (port == null && portURI != null) {
            int h = portURI.indexOf('#');
 //           String portNamespace = portURI.substring(0, h);
            String serviceName;
            String portName;

            String fragment = portURI.substring(h + 1);
            if (fragment.startsWith("wsdl.endpoint(") && fragment.endsWith(")")) {
                fragment = fragment.substring(14, fragment.length() - 1);
                int slash = fragment.indexOf('/');
                if (slash != -1) {
                    serviceName = fragment.substring(0, slash);
                    portName = fragment.substring(slash + 1);
                } else {
                    serviceName = null;
                    portName = fragment;
                }
            } else {
                serviceName = null;
                portName = fragment;
            }
            // FIXME need to find out how to get wsdl and what context to use --- terrible hack attack!
            URL wsdlurl = Thread.currentThread().getContextClassLoader().getResource(wsdlLocation);
            WSDLFactory factory = WSDLFactory.newInstance();
            WSDLReader reader = factory.newWSDLReader();
            reader.setFeature("javax.wsdl.verbose", false);
            InputSource input = new InputSource(wsdlurl.openStream());
            Definition wsdlDef = reader.readWSDL(wsdlurl.toString(), input);
            definitions = new LinkedList<Definition>();
            definitions.add(wsdlDef);
            // FIXME all the above needs to better addressed.

            Definition definition = null;
            Port thePort = null;
            Service service = null;
            for (Definition def : definitions) {

                // Find the port with the given name
                for (Service serv : (Collection<Service>) def.getServices().values()) {
                    QName sqn = serv.getQName();
                    if (serviceName != null && !serviceName.equals(sqn.getLocalPart())) {
                        continue;
                    }

                    Port p = serv.getPort(portName);
                    if (p != null) {
                        service = serv;
                        definition = def;
                        thePort = p;
                        break;
                    }
                }
            }
            if (thePort == null) {
                throw new IllegalArgumentException("Cannot find WSDL port " + portURI);

            }
            return new WebServiceBinding(definition, thePort, port, portURI, service);
        }
        // FIXME
        return null;

    }

}
