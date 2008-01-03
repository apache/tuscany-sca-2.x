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

package org.apache.tuscany.sca.implementation.openjpa;

import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openjpa.persistence.PersistenceUnitInfoImpl;
import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.implementation.openjpa.impl.JPAImplementationFactoryImpl;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;

public class JPAImplementationProcessor implements StAXArtifactProcessor<JPAImplementation> {
    private final QName QNAME = new QName(Constants.SCA10_TUSCANY_NS, "implementation.jpa");
    private QName DS = new QName(Constants.SCA10_TUSCANY_NS, "datasource");

    private JPAImplementationFactory jpaFactory;
    private Log log = LogFactory.getLog(this.getClass());
    private StAXArtifactProcessor<PersistenceUnitInfoImpl> puiiProcessor;

    public JPAImplementationProcessor(ModelFactoryExtensionPoint modelFactories) {
        AssemblyFactory assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        JavaInterfaceFactory javaFactory = modelFactories.getFactory(JavaInterfaceFactory.class);

        this.jpaFactory = new JPAImplementationFactoryImpl(assemblyFactory, javaFactory);
        this.puiiProcessor = new PersistenceUnitInfoImplProcessor(modelFactories);
    }

    public Class<JPAImplementation> getModelType() {
        return JPAImplementation.class;
    }

    public QName getArtifactType() {
        return QNAME;
    }

    public JPAImplementation read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        assert QNAME.equals(reader.getName());
        JPAImplementation implementation = jpaFactory.createOpenJpaImplementation();

        implementation.setPersistenceUnitInfoImpl(puiiProcessor.read(reader));
        Properties dsmeta = new Properties();
        do {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (!reader.getName().equals(DS)) {
                    dsmeta.setProperty(reader.getName().getLocalPart(), reader.getElementText());
                }
            } else if (event == XMLStreamConstants.END_ELEMENT && reader.getName().equals(DS)) {
                implementation.setDataSourceMeta(dsmeta);
                break;
            }
        } while (true);

        return implementation;

    }

    public void write(JPAImplementation model, XMLStreamWriter writer) throws ContributionWriteException, XMLStreamException {
        // TODO Auto-generated method stub

    }
    
    public void resolve(JPAImplementation model, ModelResolver resolver) throws ContributionResolveException {

    }

}
