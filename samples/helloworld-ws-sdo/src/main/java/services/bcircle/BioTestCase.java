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

package services.bcircle;

import model.sdo.EntityFactory;
import model.sdo.Laboratory;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;

public class BioTestCase {

    /**
     * @param args
     */
    public static void main(String[] args) {
        BiochemicalCircle biochemicalCircl = new BiochemicalCircleImpl();
        Laboratory lab2 = biochemicalCircl.getLaboratory("Lab2"); //This invocation without use SCA works ok.

        NodeFactory factory = NodeFactory.newInstance();
        String contribution = ContributionLocationHelper.getContributionLocation(BioTestCase.class);
        Node node =
            factory.createNode("resources/clinicalLaboratory.composite", new Contribution("clinical", contribution))
                .start();

        BiochemicalCircle biochemicalCircle = node.getService(BiochemicalCircle.class, "BiochemicalCircleComponent");

        Laboratory lab = EntityFactory.INSTANCE.createLaboratory();
        lab.setName("lab2");
        biochemicalCircle.setLaboratory(lab); // this invocation works ok too

        lab = biochemicalCircle.getLaboratory("Lab2"); // here I have an exception posted below.

        //here I wait a moment before close scaDomain
        System.out.println(lab.getName());

        node.stop();

    }
}
