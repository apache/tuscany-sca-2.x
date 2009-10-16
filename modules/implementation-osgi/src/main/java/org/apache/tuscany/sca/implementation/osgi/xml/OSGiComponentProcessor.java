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

package org.apache.tuscany.sca.implementation.osgi.xml;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;

/*
<scr:component name="CalculatorComponent" 
               xmlns:scr="http://www.osgi.org/xmlns/scr/v1.0.0">
    <implementation class="calculator.dosgi.CalculatorServiceDSImpl" />
    <service>
        <provide interface="calculator.dosgi.CalculatorService" />
    </service>

    <reference name="addService" interface="calculator.dosgi.operations.AddService" bind="setAddService" unbind="unsetAddService"
        policy="dynamic" />
    <reference name="subtractService" interface="calculator.dosgi.operations.SubtractService" bind="setSubtractService"
        unbind="unsetSubtractService" policy="dynamic" />
    <reference name="multiplyService" interface="calculator.dosgi.operations.MultiplyService" bind="setMultiplyService"
        unbind="unsetMultiplyService" policy="dynamic" />
    <reference name="divideService" interface="calculator.dosgi.operations.DivideService" bind="setDivideService"
        unbind="unsetDivideService" policy="dynamic" />

</scr:component>
*/
public class OSGiComponentProcessor implements StAXArtifactProcessor<ComponentType> {
    private static final String OSGI_SCR_NS = "http://www.osgi.org/xmlns/scr/v1.0.0";
    private static final String COMPOMENT = "component";
    private static final QName COMPONENT_QNAME = new QName(OSGI_SCR_NS, COMPOMENT);

    private AssemblyFactory factory;
    private JavaInterfaceFactory javaInterfaceFactory;
    

    public OSGiComponentProcessor(FactoryExtensionPoint modelFactories) {
        this.factory = modelFactories.getFactory(AssemblyFactory.class);
        this.javaInterfaceFactory = modelFactories.getFactory(JavaInterfaceFactory.class);
    }

    public ComponentType read(XMLStreamReader reader, ProcessorContext context) throws XMLStreamException {
        int event = reader.getEventType();
        ComponentType componentType = factory.createComponentType();
        while (true) {
            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    QName name = reader.getName();
                    if (COMPONENT_QNAME.equals(name)) {
                        //
                    } else if ("reference".equals(name.getLocalPart())) {
                        Reference ref = factory.createReference();
                        ref.setName(reader.getAttributeValue(null, "name"));
                        String interfaceName = reader.getAttributeValue(null, "interface");
                        InterfaceContract interfaceContract = javaInterfaceFactory.createJavaInterfaceContract();
                        ref.setInterfaceContract(interfaceContract);
                        JavaInterface javaInterface = javaInterfaceFactory.createJavaInterface();
                        javaInterface.setUnresolved(true);
                        javaInterface.setName(interfaceName);
                        interfaceContract.setInterface(javaInterface);
                        componentType.getReferences().add(ref);
                    } else if ("provide".equals(name.getLocalPart())) {
                        Service service = factory.createService();
                        String interfaceName = reader.getAttributeValue(null, "interface");
                        InterfaceContract interfaceContract = javaInterfaceFactory.createJavaInterfaceContract();
                        service.setInterfaceContract(interfaceContract);
                        JavaInterface javaInterface = javaInterfaceFactory.createJavaInterface();
                        javaInterface.setUnresolved(true);
                        javaInterface.setName(interfaceName);
                        interfaceContract.setInterface(javaInterface);
                        componentType.getServices().add(service);
                    } else if ("property".equals(name.getLocalPart())) {
                        String propName = reader.getAttributeValue(null, "name");
                        String propValue = reader.getAttributeValue(null, "value");
                        String propType = reader.getAttributeValue(null, "type");
                        if (propType == null) {
                            propType = "String";
                        }
                        if (propValue == null) {
                            propValue = reader.getElementText();
                        }
                        if (propValue != null) {
                            propValue = propValue.trim();
                        }
                        Object prop = propValue;
                        if ("Integer".equals(propType)) {
                            prop = Integer.valueOf(propValue);
                        } else if ("Long".equals(propType)) {
                            prop = Long.valueOf(propValue);
                        } else if ("Float".equals(propType)) {
                            prop = Float.valueOf(propValue);
                        } else if ("Double".equals(propType)) {
                            prop = Double.valueOf(propValue);
                        } else if ("Short".equals(propType)) {
                            prop = Short.valueOf(propValue);
                        } else if ("Character".equals(propType)) {
                            prop = propValue.charAt(0);
                        } else if ("Byte".equals(propType)) {
                            prop = Byte.valueOf(propValue);
                        } else if ("Boolean".equals(propType)) {
                            prop = Boolean.valueOf(propValue);
                        }
                        Property property = factory.createProperty();
                        property.setName(propName);
                        property.setValue(prop);
                        componentType.getProperties().add(property);
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    name = reader.getName();
                    if (COMPONENT_QNAME.equals(name)) {
                        return componentType;
                    }
                    break;
            }
            if (reader.hasNext()) {
                event = reader.next();
            } else {
                return componentType;
            }
        }
    }

    public QName getArtifactType() {
        return COMPONENT_QNAME;
    }

    public void write(ComponentType model, XMLStreamWriter writer, ProcessorContext context) throws ContributionWriteException,
        XMLStreamException {
        // TODO: To be implemented
    }

    public Class<ComponentType> getModelType() {
        return ComponentType.class;
    }

    public void resolve(ComponentType model, ModelResolver resolver, ProcessorContext context) throws ContributionResolveException {
        // TODO: To be implemented
    }
}
