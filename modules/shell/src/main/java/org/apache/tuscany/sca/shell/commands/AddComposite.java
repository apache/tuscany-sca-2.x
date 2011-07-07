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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;

import javax.xml.stream.XMLStreamException;

import jline.Completor;
import jline.FileNameCompletor;
import jline.NullCompletor;

import org.apache.tuscany.sca.common.java.io.IOHelper;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.monitor.ValidationException;
import org.apache.tuscany.sca.runtime.ActivationException;
import org.apache.tuscany.sca.shell.Command;
import org.apache.tuscany.sca.shell.Shell;
import org.apache.tuscany.sca.shell.jline.ICURICompletor;

public class AddComposite implements Command {

    private Shell shell;
    
    public AddComposite(Shell shell) {
        this.shell = shell;
    }

    @Override
    public String getName() {
        return "addComposite";
    }

    @Override
    public String getShortHelp() {
        return "addComposite <contributionURI> <compositeURI>";
    }

    @Override
    public String getHelp() {
        StringBuilder helpText = new StringBuilder();
        helpText.append("Adds a deployable composite to an installed contribution.");
        helpText.append("\n");
        helpText.append("Arguments:");
        helpText.append("   contributionURI - (required) the URI of the installed contribution");
        helpText.append("   compositeURL    - (required) the URL to an external composite file");
        return helpText.toString();
    }

    @Override
    public Completor[] getCompletors() {
        return new Completor[]{new ICURICompletor(shell), new FileNameCompletor(), new NullCompletor()};
    }

    @Override
    public boolean invoke(String[] args) throws ContributionReadException, FileNotFoundException, XMLStreamException, ActivationException, ValidationException, URISyntaxException {
        if (args.length != 2) {
            System.err.println("Wrong number of args");
            System.err.println(getShortHelp());
            return true;
        }
        if (shell.getNode().getInstalledContributionURIs().contains(args[0])) {
            System.err.println("contribution not installed: " + args[0]);
        }
        
        File f = new File(IOHelper.getLocationAsURL(args[1]).toURI());
        shell.getNode().addDeploymentComposite(args[0], new FileReader(f));
        return true;
    }
    
}
