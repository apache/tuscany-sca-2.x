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

package scatours;

import static scatours.launcher.LauncherUtil.locate;

import org.apache.activemq.broker.BrokerService;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;

public class CurrencyConverterJMSLauncher {

    public static void main(String[] args) throws Exception {
        final BrokerService jmsBroker = new BrokerService();
        jmsBroker.setPersistent(false);
        jmsBroker.setUseJmx(false);
        jmsBroker.addConnector("tcp://localhost:61619");
        jmsBroker.start();

        Contribution currencyJMSContribution = locate("currency-jms");
        Contribution currencyContribution = locate("currency");

        Node node =
            NodeFactory.getInstance().createNode("currency-converter-jms.composite",
                                                       currencyContribution,
                                                       currencyJMSContribution);
        node.start();

        System.out.println("Node started - Press enter to shutdown.");
        System.in.read();

        node.stop();
        jmsBroker.stop();
    }
}
