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
package echo.provider.policy;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ClassReference;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;

/**
 *
 */
public abstract class EchoBindingPolicyProcessor<T extends EchoBindingPolicy> implements StAXArtifactProcessor<T> {
    public static final String ENCRYPTION = "Encryption";
    
    public QName getArtifactType() {
        return new QName("http://sample/policy", "echoBindingPolicy");
    }

    public T read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        String name = reader.getAttributeValue(null, "name");
        if ( name != null && name.equals(ENCRYPTION) ) {
            EchoBindingEncryptionPolicy policy = new EchoBindingEncryptionPolicy();
            policy.setEncryptionStrategyClassName(reader.getAttributeValue(null, "strategy"));
            return (T)policy;
        }
        return null;
    }

    public void write(T arg0, XMLStreamWriter arg1) throws ContributionWriteException,
                                                        XMLStreamException {
    }

    public void resolve(T policy, ModelResolver resolver) throws ContributionResolveException {
        if ( policy instanceof EchoBindingEncryptionPolicy ) {
            EchoBindingEncryptionPolicy ePolicy = (EchoBindingEncryptionPolicy)policy;
        
            ClassReference classReference = new ClassReference(ePolicy.getEncryptionStrategyClassName());
            classReference = resolver.resolveModel(ClassReference.class, classReference);
            Class javaClass = classReference.getJavaClass();
            if (javaClass == null) {
                //throw new ContributionResolveException(new ClassNotFoundException(ePolicy.getEncryptionStrategyClass()));
            }
            //ePolicy.setStrategyClass(javaClass);
            //FIXME: need to resolve this thro resolver
            try {
                ePolicy.setStrategyClass((Class<? extends EncryptionStrategy>)Class.forName(ePolicy.getEncryptionStrategyClassName()));
            } catch ( Exception e ) {
                throw new ContributionResolveException(e);
            }
            ePolicy.setStrategyClass(ePolicy.getStrategyClass());
            ePolicy.setUnresolved(false);
        }
    }
}
