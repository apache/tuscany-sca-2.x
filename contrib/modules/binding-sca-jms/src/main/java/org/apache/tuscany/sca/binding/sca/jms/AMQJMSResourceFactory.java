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

package org.apache.tuscany.sca.binding.sca.jms;

import javax.jms.JMSException;
import javax.naming.NamingException;

import org.apache.activemq.ActiveMQConnection;
import org.apache.tuscany.sca.binding.jms.provider.JMSResourceFactoryImpl;

public class AMQJMSResourceFactory extends JMSResourceFactoryImpl {

    public AMQJMSResourceFactory(String connectionFactoryName, String initialContextFactoryName, String jndiURL) {
        super(connectionFactoryName, initialContextFactoryName, jndiURL);
    }

    @Override
    protected void createConnection() throws NamingException, JMSException {
        super.createConnection();
        ((ActiveMQConnection)connection).setWatchTopicAdvisories(false);
    }
}
