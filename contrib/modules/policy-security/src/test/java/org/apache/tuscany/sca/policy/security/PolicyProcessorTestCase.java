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

package org.apache.tuscany.sca.policy.security;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import junit.framework.Assert;

import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.policy.authorization.AuthorizationPolicy;
import org.apache.tuscany.sca.policy.authorization.AuthorizationPolicyProcessor;
import org.apache.tuscany.sca.policy.identity.SecurityIdentityPolicy;
import org.apache.tuscany.sca.policy.identity.SecurityIdentityPolicyProcessor;
import org.junit.Test;

/**
 * @version $Rev$ $Date$
 */
public class PolicyProcessorTestCase {
    private final static List<String> SEQ =
        Arrays.asList("permitAll",
                      "allow [r1, r2]",
                      "denyAll",
                      "runAs admin",
                      "useCallerIdentity",
                      "permitAll",
                      "allow [r1, r2]",
                      "denyAll",
                      "runAs admin");

    @Test
    public void testRead() throws Exception {
        List<String> results = new ArrayList<String>();
        Map<QName, StAXArtifactProcessor> processors = new HashMap<QName, StAXArtifactProcessor>();
        processors.put(AuthorizationPolicy.NAME, new AuthorizationPolicyProcessor(null,null));
        processors.put(SecurityIdentityPolicy.NAME, new SecurityIdentityPolicyProcessor(null,null));
        processors.put(new QName(Constants.SCA10_NS, "allow"), new AuthorizationPolicyProcessor(null,null));
        processors.put(new QName(Constants.SCA10_NS, "permitAll"), new AuthorizationPolicyProcessor(null,null));
        processors.put(new QName(Constants.SCA10_NS, "denyAll"), new AuthorizationPolicyProcessor(null,null));
        processors.put(new QName(Constants.SCA10_NS, "runAs"), new SecurityIdentityPolicyProcessor(null,null));
        InputStream is = getClass().getResourceAsStream("mock_policy_definitions.xml");
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(is);
        while (true) {
            int event = reader.getEventType();
            if (event == XMLStreamConstants.START_ELEMENT) {
                if ("policySet".equals(reader.getName().getLocalPart())) {
                    reader.nextTag();
                    results.add(processors.get(reader.getName()).read(reader).toString());
                }
            }
            if (reader.hasNext()) {
                reader.next();
            } else {
                break;
            }
        }
        Assert.assertEquals(SEQ, results);
    }
}
