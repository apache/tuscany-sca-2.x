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

package org.apache.tuscany.sca.shell.commands;

import jline.Completor;
import jline.NullCompletor;

import org.apache.tuscany.sca.shell.Command;
import org.apache.tuscany.sca.shell.Shell;
import org.apache.tuscany.sca.shell.jline.CompositeURICompletor;
import org.apache.tuscany.sca.shell.jline.ICURICompletor;

public class Stop implements Command {

    private Shell shell;
    
    public Stop(Shell shell) {
        this.shell = shell;
    }

    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public String getShortHelp() {
        return "stop <curi> <compositeUri>";
    }

    @Override
    public String getHelp() {
        StringBuilder helpText = new StringBuilder();
        helpText.append("   Stop a composite.\n");
        helpText.append("\n");
        helpText.append("   The composite is removed from the domain composite, along with all the components, wires,\n");
        helpText.append("   services and references originally added when the composite was started.\n");
        helpText.append("\n");
        helpText.append("   Arguments:\n");
        helpText.append("      curi         - (required) the URI of an installed contribution\n");
        helpText.append("      compositeUri - (required) the URI of a composite within the contribution to stop\n");
        return helpText.toString();
    }

    @Override
    public Completor[] getCompletors() {
        return new Completor[]{new ICURICompletor(shell), new CompositeURICompletor(shell), new NullCompletor()};
    }

    @Override
    public boolean invoke(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Wrong number of args");
            System.err.println(getShortHelp());
            return true;
        }
        shell.getNode().stopComposite(args[0], args[1]);
        return true;
    }

}
