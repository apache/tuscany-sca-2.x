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

package org.apache.tuscany.sca.shell.jline;

import java.util.ArrayList;
import java.util.List;

import jline.SimpleCompletor;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.monitor.ValidationException;
import org.apache.tuscany.sca.shell.Shell;

/**
 * A Completor that uses the composite URIs within a Contribution
 * Will only work if the argument before the composite URI is a contribution URI
 */
public class CompositeURICompletor extends SimpleCompletor {

    private Shell shell;

    public CompositeURICompletor(Shell shell) {
        super("");
        this.shell = shell;
    }
    
    @Override
    public int complete(final String buffer, final int cursor, final List clist) {
        if (shell.getNode() == null) {
            return -1;
        }
       Contribution c;
       try {
           c = shell.getNode().getContribution(getContributionURI());
       } catch (Exception e) {
           return super.complete(buffer, cursor, clist);   
       }
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
