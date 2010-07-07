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

package org.apache.tuscany.sca.shell;

import java.util.ArrayList;
import java.util.List;

import jline.SimpleCompletor;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.node2.Node;

/**
 * A Completor that uses the composite URIs within a Contribution
 * Will only work if the argument before the composite URI is a contribution URI
 */
public class CompositeURICompletor extends SimpleCompletor {

    private Node node;

    public CompositeURICompletor(Node node) {
        super("");
        this.node = node;
    }
    
    @Override
    public int complete(final String buffer, final int cursor, final List clist) {
       Contribution c = node.getInstalledContribution(getContributionURI());
       if (c == null) {
           return -1;
       }

       List<String> cus = new ArrayList<String>();
       for (Artifact a : c.getArtifacts()) {
           if (a.getModel() instanceof Composite) {
               cus.add(((Composite)a.getModel()).getURI());
           }
       }
       setCandidateStrings(cus.toArray(new String[cus.size()]));
       return super.complete(buffer, cursor, clist);   
    }

    protected String getContributionURI() {
        /* A little hacky to use a static but i can't see how else to get the contribution URI */
        return TShellCompletor.lastArg;
    }
}
