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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import javax.xml.stream.XMLStreamException;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.common.java.io.IOHelper;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.monitor.ValidationException;
import org.apache.tuscany.sca.node2.Node;
import org.apache.tuscany.sca.node2.NodeFactory;
import org.apache.tuscany.sca.runtime.ActivationException;
import org.apache.tuscany.sca.runtime.Version;

/**
 * A little SCA command shell.
 */
public class Shell {
    
    Node node;
    private boolean useJline;
    final List<String> history = new ArrayList<String>();
    static final String[] COMMANDS = new String[] {"addDeploymentComposite", "addToDomainLevelComposite", "help",
                                                           "install", "listDeployedCompostes",
                                                           "printDomainLevelComposite", "listInstalledContributions",
                                                           "removeFromDomainLevelComposite", "remove", "status", "stop"};

    public static void main(final String[] args) throws Exception {
        boolean useJline = !Arrays.asList(args).contains("-nojline");
        new Shell(args.length > 0 ? args[0] : "default", useJline).run();
    }

    public Shell(String domainURI, boolean useJLine) {
        this.node = NodeFactory.newInstance().createNode(domainURI);
        this.useJline = useJLine;
    }

    boolean addDeploymentComposite(final String curi, String contentURL) throws ContributionReadException, XMLStreamException, ActivationException, ValidationException, IOException {
        node.addDeploymentComposite(curi, new StringReader(readContents(contentURL)));
        return true;
    }

    boolean addToDomainLevelComposite(final String uri) throws ContributionReadException, ActivationException, ValidationException {
        node.addToDomainLevelComposite(uri);
        return true;
    }

    boolean install(final String cloc, final List<String> toks) throws ContributionReadException, ActivationException, ValidationException {
        boolean runDeployables = !toks.contains("-norun");
        String uri;
        if (toks.contains("-uri")) {
            uri = toks.get(toks.indexOf("-uri")+1);
        } else {
            uri = getDefaultURI(cloc);
            out.println("installing at: " + uri);
        }
        String metaDataURL = null;
        if (toks.contains("-metadata")) {
            metaDataURL = toks.get(toks.indexOf("-metadata")+1);
        }
        List<String> duris = null;
        if (toks.contains("-duris")) {
            duris = Arrays.asList(toks.get(toks.indexOf("-duris")+1).split(","));
        }
        
        node.installContribution(uri, cloc, metaDataURL, duris, runDeployables);
        return true;
    }

    private String getDefaultURI(String contributionURL) {
        int lastDot = contributionURL.lastIndexOf('.');
        int lastSep = contributionURL.lastIndexOf("/");
        String uri = contributionURL;
        if (lastDot > -1 && lastSep > -1 && lastDot > lastSep) {
            uri = contributionURL.substring(lastSep+1, lastDot);
        } else {
            try {
                File f = new File(contributionURL);
                if ("classes".equals(f.getName()) && "target".equals(f.getParentFile().getName())) {
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
            out.println(uri.substring(curi.length()+1));
        }
        return true;
    }

    boolean listInstalledContributions() throws ContributionReadException, ActivationException, ValidationException {
        for (String uri : node.getInstalledContributions()) {
            out.println(uri);
            listComposites(uri);
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

    boolean listComposites(final String curi) {
        Contribution c = node.getInstalledContribution(curi);
        for (Artifact a : c.getArtifacts()) {
            if (a.getModel() instanceof Composite) {
                out.println(((Composite)a.getModel()).getName());
            }
        }
        return true;
    }

    boolean help() {
        out.println("Apache Tuscany Shell (" + Version.getVersion() + " " + Version.getRevsion() + " " + Version.getBuildTime() + ")");
        out.println("Commands:");
        out.println();
        out.println("   help");
        out.println("   install <contributionURL> [-uri <uri> -norun -metadata <url> -duris <uri,uri,...>]");
        out.println("   remove <contributionURI>");
        out.println("   addDeploymentComposite <contributionURI> <content>");
        out.println("   addToDomainLevelComposite <contributionURI/compositeURI>");
        out.println("   removeFromDomainLevelComposite <contributionURI/compositeURI>");
        out.println("   listDeployedCompostes <contributionURI>");
        out.println("   listInstalledContributions");
        out.println("   printDomainLevelComposite");
        out.println("   status");
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
    
    List<String> read(Object r) throws IOException {
        out.print("=> ");
        final String l;
        if (useJline) {
            l = JLine.readLine(r);
        } else {
            l = ((BufferedReader)r).readLine();
            history.add(l);
        }
//        history.add(l);
        return Arrays.asList(l != null? l.trim().split(" ") : "stop".split(" "));
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
            return install(toks.get(1), toks);
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
        if (op.equals("")) return new Callable<Boolean>() { public Boolean call() {
            return true;
        }};
        return new Callable<Boolean>() { public Boolean call() {
            out.println("unknown command");
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
        help();
        Object reader;
        if (useJline) {
            reader = JLine.createJLineReader(this);
        } else {
            reader = new BufferedReader(new InputStreamReader(in));
        }
        while(apply(eval(read(reader))));
    }

    String readContents(String location) throws IOException {
        URL url = IOHelper.getLocationAsURL(location);
        InputStream is = IOHelper.openStream(url);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder builder = new StringBuilder(8192);
            for(String line=br.readLine(); line!=null; line=br.readLine()) {
                builder.append(line);
                builder.append('\n');
            }
            return builder.toString();
        } finally {
            IOHelper.close(is);
        }
     }

}
