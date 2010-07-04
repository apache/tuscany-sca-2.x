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

import static java.lang.System.in;
import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import javax.xml.stream.XMLStreamException;

import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.monitor.ValidationException;
import org.apache.tuscany.sca.node2.Node;
import org.apache.tuscany.sca.node2.NodeFactory;
import org.apache.tuscany.sca.runtime.ActivationException;


/**
 * A little SCA command shell.
 */
public class Shell {
    
    final NodeFactory nodeFactory = NodeFactory.newInstance();
    Node node;
    
    public Shell(String domainURI) {
        this.node = nodeFactory.createNode(domainURI);
    }

    final List<String> history = new ArrayList<String>();
    
    public static void main(final String[] args) throws Exception {
        new Shell(args.length > 0 ? args[0] : "default").run();
    }

    boolean addDeploymentComposite(final String curi, String content) throws ContributionReadException, XMLStreamException, ActivationException, ValidationException {
        node.addDeploymentComposite(curi, new StringReader(content));
        return true;
    }

    boolean addToDomainLevelComposite(final String uri) throws ContributionReadException, ActivationException, ValidationException {
        node.addToDomainLevelComposite(uri);
        return true;
    }

    boolean install(final String cloc) throws ContributionReadException, ActivationException, ValidationException {
        String uri = getURI(cloc);
        node.installContribution(getURI(cloc), cloc, null, null, true);
        out.println("installed: " + uri);
        return true;
    }

    private String getURI(String contributionURL) {
        int lastDot = contributionURL.lastIndexOf('.');
        int lastSep = contributionURL.lastIndexOf("/");
        String uri = contributionURL;
        if (lastDot > -1 && lastSep > -1 && lastDot > lastSep) {
            uri = contributionURL.substring(lastSep+1, lastDot);
        } else {
            try {
                File f = new File(contributionURL);
                if ("classes".equals(f.getName())) {
                    uri = f.getParentFile().getParentFile().getName();                   
                }
            } catch (Exception e) {
                // ignore
            }
        }
        return uri;
    }

    boolean listDeployedCompostes(String curi) throws ContributionReadException, ActivationException, ValidationException {
        for (String uri : node.getDeployedCompostes(curi)) {
//            out.println(uri.substring(curi.length()+1));
            out.println(uri);
        }
        return true;
    }

    boolean listInstalledContributions() throws ContributionReadException, ActivationException, ValidationException {
        for (String uri : node.getInstalledContributions()) {
            out.println(uri);
        }
        return true;
    }

    boolean printDomainLevelComposite() throws ContributionReadException, ActivationException, ValidationException {
        out.println(node.getDomainLevelCompositeAsString());
        return true;
    }
    
    boolean getQNameDefinition(final String curi, String definintion, String symbolSpace) throws ContributionReadException, ActivationException, ValidationException {
        // TODO:
        return true;
    }

    boolean remove(final String curi) throws ContributionReadException, ActivationException, ValidationException {
        node.removeContribution(curi);
        return true;
    }

    boolean removeFromDomainLevelComposite(final String uri) throws ContributionReadException, ActivationException, ValidationException {
        node.removeFromDomainLevelComposite(uri);
        return true;
    }

    boolean help() {
        out.println("Commands:");
        out.println();
        out.println("   install <contributionURL>");
        out.println("   remove <contributionURL>");
        out.println("   addDeploymentComposite <contributionURL> <content>");
        out.println("   addToDomainLevelComposite <contributionURI/compositeURI>");
        out.println("   removeFromDomainLevelComposite <contributionURI/compositeURI>");
        out.println("   listDeployedCompostes <contributionURI>");
        out.println("   listInstalledContributions");
        out.println("   printDomainLevelComposite");
        out.println("   stop");
        out.println();
        return true;
    }

    boolean stop() {
        node.stop();
        return false;
    }

    boolean status() {
        out.println("Domain: " + node.getDomainName());
        out.println("   installed contributions: " + node.getInstalledContributions().size());
        int x = 0;
        for (String curi : node.getInstalledContributions()) {
            x += node.getDeployedCompostes(curi).size();
        }
        out.println("   deployed composites: " + x);
        return true;
    }

    boolean history() {
        for (String l: history)
            out.println(l);
        return true;
    }

    List<String> read(final BufferedReader r) throws IOException {
        out.print("=> ");
        final String l = r.readLine();
        history.add(l);
        return Arrays.asList(l != null? l.trim().split(" ") : "bye".split(" "));
    }
       
    Callable<Boolean> eval(final List<String> toks) {
        final String op = toks.get(0);

        if (op.equals("addDeploymentComposite")) return new Callable<Boolean>() { public Boolean call() throws Exception {
            return addDeploymentComposite(toks.get(1), toks.get(2));
        }};
        if (op.equals("addToDomainLevelComposite")) return new Callable<Boolean>() { public Boolean call() throws Exception {
            return addToDomainLevelComposite(toks.get(1));
        }};
        if (op.equals("install")) return new Callable<Boolean>() { public Boolean call() throws Exception {
            return install(toks.get(1));
        }};
        if (op.equals("listDeployedCompostes")) return new Callable<Boolean>() { public Boolean call() throws Exception {
            return listDeployedCompostes(toks.get(1));
        }};
        if (op.equals("printDomainLevelComposite")) return new Callable<Boolean>() { public Boolean call() throws Exception {
            return printDomainLevelComposite();
        }};
        if (op.equals("listInstalledContributions")) return new Callable<Boolean>() { public Boolean call() throws Exception {
            return listInstalledContributions();
        }};
        if (op.equals("getQNameDefinition")) return new Callable<Boolean>() { public Boolean call() throws Exception {
            return getQNameDefinition(toks.get(1), toks.get(2), toks.get(3));
        }};
        if (op.equals("remove")) return new Callable<Boolean>() { public Boolean call() throws Exception {
            return remove(toks.get(1));
        }};
        if (op.equals("removeFromDomainLevelComposite")) return new Callable<Boolean>() { public Boolean call() throws Exception {
            return removeFromDomainLevelComposite(toks.get(1));
        }};
        if (op.equals("help")) return new Callable<Boolean>() { public Boolean call() {
            return help();
        }};
        if (op.equals("stop")) return new Callable<Boolean>() { public Boolean call() {
            return stop();
        }};
        if (op.equals("status")) return new Callable<Boolean>() { public Boolean call() {
            return status();
        }};
        if (op.equals("history")) return new Callable<Boolean>() { public Boolean call() {
            return history();
        }};
        return new Callable<Boolean>() { public Boolean call() {
            return true;
        }};
    }

    boolean apply(final Callable<Boolean> func) {
        try {
            return func.call();
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public void run() throws IOException {
        final BufferedReader r = new BufferedReader(new InputStreamReader(in));
        while(apply(eval(read(r))));
    }
}
