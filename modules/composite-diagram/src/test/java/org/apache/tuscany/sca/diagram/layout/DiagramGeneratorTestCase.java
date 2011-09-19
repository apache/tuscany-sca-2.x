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
package org.apache.tuscany.sca.diagram.layout;

import java.io.File;
import java.io.FileWriter;

import org.apache.tuscany.sca.diagram.main.Main;
import org.apache.tuscany.sca.diagram.test.HelloWorld;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.configuration.NodeConfiguration;
import org.junit.Test;

public class DiagramGeneratorTestCase {

    @Test
    public final void testFiles() throws Exception {
        for (File xml : new File("src/test/resources/input").listFiles()) {
            if (xml.getName().endsWith(".xml")) {
                System.out.println(xml);
                Main.generate(new File("target"), null, true, false, false, xml.toString());
            }
        }
    }

    @Test
    public final void testNode() throws Exception {
        NodeConfiguration config = NodeFactory.getInstance().createNodeConfiguration();
        config.addContribution(new File("target/test-classes/contribution").toURI().toURL());

        String svg = Main.generateDiagram(config, HelloWorld.class.getClassLoader(), null);
        
        System.out.println(svg);
        FileWriter fw = new FileWriter("target/node.svg");
        fw.write(svg);
        fw.close();
    }

}
