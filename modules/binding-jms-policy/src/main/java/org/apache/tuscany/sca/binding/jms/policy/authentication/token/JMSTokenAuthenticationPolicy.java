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
package org.apache.tuscany.sca.binding.jms.policy.authentication.token;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.policy.Policy;

/**
 * Implementation for policies that could be injected as parameter
 * into the axis2config.
 *
 * @version $Rev$ $Date$
 */
public class JMSTokenAuthenticationPolicy implements Policy {
    public static final QName JMS_TOKEN_AUTHENTICATION_POLICY_QNAME = new QName(Constants.SCA10_TUSCANY_NS, "jmsTokenAuthentication");
    public static final String JMS_TOKEN_AUTHENTICATION_TOKEN_NAME = "tokenName";

    private QName tokenName;

    public QName getTokenName() {
        return tokenName;
    }
    
    public void setTokenName(QName tokenName) {
        this.tokenName = tokenName;
    }
    
    public QName getSchemaName() {
        return JMS_TOKEN_AUTHENTICATION_POLICY_QNAME;
    }

    public boolean isUnresolved() {
        return false;
    }

    public void setUnresolved(boolean unresolved) {
    }
}
