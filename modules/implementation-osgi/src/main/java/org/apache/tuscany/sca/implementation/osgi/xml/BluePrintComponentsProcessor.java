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
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.monitor.Monitor;

/*
<!-- A sample module-context.xml for OSGI RFC 124 (BluePrint Service) -->
<components xmlns="http://www.osgi.org/xmlns/blueprint/v1.0.0">
    <component id="CalculatorComponent" class="calculator.dosgi.impl.CalculatorServiceImpl">
        <property name="addService" ref="AddService" />
        <property name="subtractService" ref="SubtractService" />
        <property name="multiplyService" ref="MultiplyService" />
        <property name="divideService" ref="DivideService" />
    </component>

    <!-- We can derive the SCA services for the implementation.osgi -->
    <service id="CalculatorService" ref="CalculatorComponent" interface="calculator.dosgi.CalculatorService">
    </service>

    <!-- We can derive the SCA references for the implementation.osgi -->
    <reference id="AddService" interface="calculator.dosgi.operations.AddService">
    </reference>
    <reference id="SubtractService" interface="calculator.dosgi.operations.SubtractService">
    </reference>
    <reference id="MultiplyService" interface="calculator.dosgi.operations.MultiplyService">
    </reference>
    <reference id="DivideService" interface="calculator.dosgi.operations.DivideService">
    </reference>

</components>
*/
public class BluePrintComponentsProcessor implements StAXArtifactProcessor<ComponentType> {
    private static final String BLUE_PRINT_NS = "http://www.osgi.org/xmlns/blueprint/v1.0.0";
    private static final String COMPOMENTS = "components";
    private static final QName COMPONENTS_QNAME = new QName(BLUE_PRINT_NS, COMPOMENTS);

    private AssemblyFactory factory;
    private JavaInterfaceFactory javaInterfaceFactory;
    private Monitor monitor;

    public BluePrintComponentsProcessor(FactoryExtensionPoint modelFactories, Monitor monitor) {
        this.monitor = monitor;
        this.factory = modelFactories.getFactory(AssemblyFactory.class);
        this.javaInterfaceFactory = modelFactories.getFactory(JavaInterfaceFactory.class);
    }

    public ComponentType read(XMLStreamReader reader) throws XMLStreamException {
        int event = reader.getEventType();
        ComponentType componentType = factory.createComponentType();
        while (true) {
            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    QName name = reader.getName();
                    if (COMPONENTS_QNAME.equals(name)) {
                        //
                    } else if ("reference".equals(name.getLocalPart())) {
                        Reference ref = factory.createReference();
                        ref.setName(reader.getAttributeValue(null, "id"));
                        String interfaceName = reader.getAttributeValue(null, "interface");
                        InterfaceContract interfaceContract = javaInterfaceFactory.createJavaInterfaceContract();
                        ref.setInterfaceContract(interfaceContract);
                        JavaInterface javaInterface = javaInterfaceFactory.createJavaInterface();
                        javaInterface.setUnresolved(true);
                        javaInterface.setName(interfaceName);
                        interfaceContract.setInterface(javaInterface);
                        componentType.getReferences().add(ref);
                    } else if ("service".equals(name.getLocalPart())) {
                        Service service = factory.createService();
                        String interfaceName = reader.getAttributeValue(null, "interface");
                        InterfaceContract interfaceContract = javaInterfaceFactory.createJavaInterfaceContract();
                        service.setInterfaceContract(interfaceContract);
                        JavaInterface javaInterface = javaInterfaceFactory.createJavaInterface();
                        javaInterface.setUnresolved(true);
                        javaInterface.setName(interfaceName);
                        interfaceContract.setInterface(javaInterface);
                        componentType.getServices().add(service);
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    name = reader.getName();
                    if (COMPONENTS_QNAME.equals(name)) {
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
        return COMPONENTS_QNAME;
    }

    public void write(ComponentType model, XMLStreamWriter writer) throws ContributionWriteException,
        XMLStreamException {
        // TODO: To be implemented
    }

    public Class<ComponentType> getModelType() {
        return ComponentType.class;
    }

    public void resolve(ComponentType model, ModelResolver resolver) throws ContributionResolveException {
        // TODO: To be implemented
    }
}
