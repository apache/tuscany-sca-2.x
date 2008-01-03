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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.openjpa.persistence.PersistenceUnitInfoImpl;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;

public class PersistenceUnitInfoImplProcessor implements StAXArtifactProcessor<PersistenceUnitInfoImpl> {
    private QName P_U = new QName(Constants.SCA10_TUSCANY_NS, "persistence-unit");
    private QName BrokerFactory = new QName(Constants.SCA10_TUSCANY_NS, "jpa.BrokerFactory");
    private QName CLASS = new QName(Constants.SCA10_TUSCANY_NS, "class");
    private QName TRAN_MD = new QName(Constants.SCA10_TUSCANY_NS, "jpa.TransactionMode");
    private QName LOG = new QName(Constants.SCA10_TUSCANY_NS, "jpa.Log");
    private QName SYNCHRON = new QName(Constants.SCA10_TUSCANY_NS, "jpa.jdbc.SynchronizeMappings");

    public PersistenceUnitInfoImplProcessor(ModelFactoryExtensionPoint modelFactories) {

    }

    public QName getArtifactType() {
        // TODO Auto-generated method stub
        return null;
    }


    public PersistenceUnitInfoImpl read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {

        PersistenceUnitInfoImpl _info = new PersistenceUnitInfoImpl();

        while (true) {
            int event = reader.next();
            switch (event) {

                case XMLStreamConstants.START_ELEMENT:
                    QName qn = reader.getName();
                    /*
                     * if (qn.equals(BrokerFactory)) {
                     * _info.setProperty("openjpa.BrokerFactory", reader
                     * .getElementText()); }
                     */
                    if (qn.equals(CLASS)) {
                        _info.addManagedClassName(reader.getElementText());

                    } else if (qn.equals(P_U)) {
                        _info.setPersistenceUnitName(reader.getAttributeValue(null, "name"));
                    } else {
                        _info.setProperty(qn.getLocalPart(), reader.getElementText());
                    } /*
                         * else if (qn.equals(LOG)) {
                         * _info.setProperty("openjpa.Log",
                         * reader.getElementText()); } else if
                         * (qn.equals(SYNCHRON)) {
                         * _info.setProperty("openjpa.jdbc.SynchronizeMappings",
                         * reader.getElementText()); }
                         */
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    if (reader.getName().equals(P_U))
                        return _info;
            }
        }
    }

    public void write(PersistenceUnitInfoImpl model, XMLStreamWriter writer) throws ContributionWriteException,
        XMLStreamException {
        // TODO Auto-generated method stub

    }

    public Class<PersistenceUnitInfoImpl> getModelType() {
        // TODO Auto-generated method stub
        return null;
    }

    public void resolve(PersistenceUnitInfoImpl model, ModelResolver resolver) throws ContributionResolveException {
        // TODO Auto-generated method stub

    }

}
