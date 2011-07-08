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
import org.apache.tuscany.sca.shell.jline.ICURICompletor;

public class Uninstall implements Command {

    private Shell shell;

    public Uninstall(Shell shell) {
        this.shell = shell;
    }
    
    @Override
    public String getName() {
        return "uninstall";
    }

    @Override
    public String getShortHelp() {
        return "uninstall <contributionURI>";
    }

    @Override
    public String getHelp() {
        StringBuilder helpText = new StringBuilder();
        helpText.append("   Uninstall an installed contribution");
        helpText.append("\n");
        helpText.append("   Arguments:");
        helpText.append("      contributionURI - (required) the URI of the contribution to uninstall");
        return helpText.toString();
    }

    @Override
    public Completor[] getCompletors() {
        return new Completor[]{new ICURICompletor(shell), new NullCompletor()};
    }

    @Override
    public boolean invoke(String[] args) throws Exception {
        shell.getNode().uninstallContribution(args[0]);
        return true;
    }

}
