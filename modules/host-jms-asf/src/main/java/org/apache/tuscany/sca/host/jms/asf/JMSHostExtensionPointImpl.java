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

package org.apache.tuscany.sca.host.jms.asf;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.host.jms.JMSHostExtensionPoint;
import org.apache.tuscany.sca.host.jms.JMSServiceListenerFactory;
import org.apache.tuscany.sca.work.WorkScheduler;

public class JMSHostExtensionPointImpl implements JMSHostExtensionPoint {

    private JMSServiceListenerFactory jmsServiceListenerFactory;

    public JMSHostExtensionPointImpl(ExtensionPointRegistry extensionPoints) {
        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        WorkScheduler workScheduler = utilities.getUtility(WorkScheduler.class);
        this.jmsServiceListenerFactory = new JMSServiceListenerFactoryImpl(workScheduler);
    }

    public JMSServiceListenerFactory getJMSServiceListenerFactory() {
        return jmsServiceListenerFactory;
    }

}
