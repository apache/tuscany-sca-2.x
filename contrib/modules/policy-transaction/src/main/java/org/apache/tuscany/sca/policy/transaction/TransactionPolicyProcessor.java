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
package org.apache.tuscany.sca.policy.transaction;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.monitor.Monitor;

/**
 * @version $Rev$ $Date$
 */
public class TransactionPolicyProcessor implements StAXArtifactProcessor<TransactionPolicy> {
    public static final String TIMEOUT = "transactionTimeout";
    public static final String ACTION = "action";
    
    public QName getArtifactType() {
        return TransactionPolicy.NAME;
    }

    public TransactionPolicyProcessor(ModelFactoryExtensionPoint modelFactories, Monitor monitor) {
    }

    public TransactionPolicy read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        TransactionPolicy txPolicy = new TransactionPolicyImpl();
        int event = reader.getEventType();
        while (reader.hasNext()) {
            event = reader.getEventType();
            switch (event) {
                case START_ELEMENT: {
                    String timeout = reader.getAttributeValue(null, TIMEOUT);
                    if (timeout != null) {
                        txPolicy.setTransactionTimeout(Integer.parseInt(timeout));
                    }
                    String action = reader.getAttributeValue(null, ACTION);
                    if (action != null) {
                        txPolicy.setAction(TransactionPolicy.Action.valueOf(action));
                    }
                    break;
                }
            }

            if (event == END_ELEMENT) {
                if (TransactionPolicy.NAME.equals(reader.getName())) {
                    break;
                }
            }

            //Read the next element
            if (reader.hasNext()) {
                reader.next();
            }
        }

        return txPolicy;
    }

    public void write(TransactionPolicy policy, XMLStreamWriter writer) throws ContributionWriteException,
        XMLStreamException {
    }

    public Class<TransactionPolicy> getModelType() {
        return TransactionPolicy.class;
    }

    public void resolve(TransactionPolicy policy, ModelResolver resolver) throws ContributionResolveException {

    }
}
