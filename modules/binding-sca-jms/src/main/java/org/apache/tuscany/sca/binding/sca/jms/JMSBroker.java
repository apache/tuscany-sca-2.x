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

import java.net.URI;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;

/**
 * The JMS broker for the JMS based SCA binding TODO: configure from a binding.jms in definitions.xml
 */
public class JMSBroker implements ModuleActivator {

    // this is static so that there is only one broker per JVM
    // if that wasn't the case then to use the vm: transport each broker
    // would need a unique name and clients would need to know the name
    private static BrokerService broker;

    public void start(ExtensionPointRegistry arg0) {
        if (broker == null) {
            broker = new BrokerService();
            broker.setPersistent(false);
            broker.setUseJmx(false);
            try {

                TransportConnector tc = broker.addConnector("tcp://localhost:0");
                tc.setDiscoveryUri(URI.create("multicast://default"));

                broker.addNetworkConnector("multicast://default");

                broker.start();

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    public void stop(ExtensionPointRegistry arg0) {
        if (broker != null) {
            try {
                broker.stop();
                broker = null;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static BrokerService getBroker() {
        return broker;
    }

}
