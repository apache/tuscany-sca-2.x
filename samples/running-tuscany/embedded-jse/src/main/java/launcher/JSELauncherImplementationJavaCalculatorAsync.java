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

package launcher;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;

/**
 * This client program shows how to create an embedded SCA runtime, load a contribution,
 * start it and locate and invoke an SCA component 
 */
public class JSELauncherImplementationJavaCalculatorAsync {
    
    public static void main(String[] args) throws Exception {
        JSELauncherImplementationJavaCalculatorAsync launcher = new JSELauncherImplementationJavaCalculatorAsync();
        launcher.launchImplementationJavaCalculator();         
    }
    
    public void launchImplementationJavaCalculator(){
        Node node = NodeFactory.newInstance().createNode(new Contribution("c1", "../../learning-more/async/calculator-contribution/target/sample-contribution-implementation-java-calculator-async.jar"));
        node.start();
        
        node.stop();
    }
    
}
