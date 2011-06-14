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
package testpolicy;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.util.logging.Level;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;

/**
 *
 * @version $Rev: 883438 $ $Date: 2009-11-23 18:07:34 +0000 (Mon, 23 Nov 2009) $
 */
public class TestPolicyProcessor implements StAXArtifactProcessor<TestPolicy> {
    
    public QName getArtifactType() {
        return TestPolicy.TEST_POLICY_QNAME;
    }
    
    public TestPolicyProcessor(FactoryExtensionPoint modelFactories) {
    }

    
    public TestPolicy read(XMLStreamReader reader, ProcessorContext context) throws ContributionReadException, XMLStreamException {
        TestPolicy policy = new TestPolicy();
        int event = reader.getEventType();
        QName name = null;
        
        
        while (reader.hasNext()) {
            event = reader.getEventType();
            switch (event) {
                case START_ELEMENT : {
                    name = reader.getName();
                    if ( name.equals("testString") ) {
                        String testString = reader.getAttributeValue(null, "testString");
                        policy.setTestString(testString);
                    }
                    break;
                }
            }
            
            if ( event == END_ELEMENT ) {
                if ( TestPolicy.TEST_POLICY_QNAME.equals(reader.getName()) ) {
                    break;
                } 
            }
            
            //Read the next element
            if (reader.hasNext()) {
                reader.next();
            }
        }
         
        return policy;
    }

    public void write(TestPolicy policy, XMLStreamWriter writer, ProcessorContext context) throws ContributionWriteException, XMLStreamException {
        // TODO
    }

    public Class<TestPolicy> getModelType() {
        return TestPolicy.class;
    }

    public void resolve(TestPolicy arg0, ModelResolver arg1, ProcessorContext context) throws ContributionResolveException {

    }
    
}
