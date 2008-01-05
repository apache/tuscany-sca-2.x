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

package launch;

import java.io.File;
import java.net.URL;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;

public class LaunchStore {
    public static void main(String[] args) throws Exception {
        System.out.println("Starting ...");
        
        URL storeContribution = new File("./target/classes").toURL();
        URL assetsContribution = new File("../assets/target/classes").toURL();
        
        SCANodeFactory nodeFactory = SCANodeFactory.newInstance();
        SCANode node = nodeFactory.createSCANode(null, "http://localhost:9998");
        URL derbyContribution = new File(System.getProperty("user.home") + "/.m2/repository/org/apache/derby/derby/10.1.2.1/derby-10.1.2.1.jar").toURL();
        URL dataAPIContribution = new File(System.getProperty("user.home") + "/.m2/repository/org/apache/tuscany/sca/tuscany-implementation-data-api/1.1-incubating-SNAPSHOT/tuscany-implementation-data-api-1.1-incubating-SNAPSHOT.jar").toURL();
        
        node.addContribution("http://org/apache/derby", derbyContribution);
        node.addContribution("http://org/apache/tuscany/implementation-data-api", dataAPIContribution);
        node.addContribution("http://assets", assetsContribution);
        node.addContribution("http://store", storeContribution);
        
        node.addToDomainLevelComposite(new QName("http://store", "store"));
        node.getDomain().start();

        System.out.println("store.composite ready for big business !!!");
        System.in.read();
        
        System.out.println("Stopping ...");
        node.stop();
        node.destroy();
        System.out.println();
    }
}
