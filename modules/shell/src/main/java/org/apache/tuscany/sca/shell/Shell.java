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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

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
import org.apache.tuscany.sca.shell.jline.JLine;

/**
 * A little SCA command shell.
 */
public class Shell {

    private boolean useJline;
    final List<String> history = new ArrayList<String>();
    private NodeFactory factory;
    private String currentDomain = "";
    private Map<String, Node> standaloneNodes = new HashMap<String, Node>();
    private Map<String, Node> nodes = new HashMap<String, Node>();

    public static final String[] COMMANDS = new String[] {"bye", "domain", "domains", "help", "install", "installed",
                                                          "load", "printDomainLevelComposite", "remove", "run", "start", "status",
                                                          "stop"};

    public static void main(final String[] args) throws Exception {
        boolean useJline = true;
        String domainURI = "default";
        for (String s : args) {
            if ("-nojline".equals(s)) {
                useJline = false;
            } else {
                domainURI = s;
            }
        }
        new Shell(domainURI, useJline).run();
    }

    public Shell(String domainURI, boolean useJLine) {
        this.factory = NodeFactory.newInstance();
        this.useJline = useJLine;
        if (domainURI != null) {
            domain(domainURI);
        }
    }

    boolean domain(final String domainURI) {
        if (domainURI.length() < 1) {
            currentDomain = "";
        } else {
            for (Node node : nodes.values()) {
                if (domainURI.equals(node.getDomainName())) {
                    currentDomain = node.getDomainName();
                    return true;
                }
            }
            Node node = factory.createNode(domainURI);
            currentDomain = node.getDomainName();
            nodes.put(currentDomain, node);
        }
        return true;
    }

    boolean domains() {
        for (Node node : nodes.values()) {
            System.out.println(node.getDomainName());
        }
        return true;
    }

    boolean install(final List<String> toks) throws ContributionReadException, ActivationException, ValidationException {
        if (getNode() == null) {
            out.println("not in domain, use domain command first");
            return true;
        }
        boolean startDeployables = toks.contains("-start");
        String metaDataURL = null;
        if (toks.contains("-metadata")) {
            metaDataURL = toks.get(toks.indexOf("-metadata") + 1);
        }
        List<String> duris = null;
        if (toks.contains("-duris")) {
            duris = Arrays.asList(toks.get(toks.indexOf("-duris") + 1).split(","));
        }

        String first = null;
        String second = null;
        for (int i = 1; i < toks.size(); i++) {
            if (toks.get(i).startsWith("-")) {
                if (!toks.get(i).equals("-start")) {
                    i++;
                }
            } else {
                if (first == null) {
                    first = toks.get(i);
                } else {
                    second = toks.get(i);
                    break;
                }
            }
        }

        String curi = null;
        String curl = null;
        if (second != null) {
            curi = first;
            curl = second;
        } else {
            curl = first;
        }

        String uri = getNode().installContribution(curi, curl, metaDataURL, duris, startDeployables);
        out.println("installed at: " + uri);
        return true;
    }

    boolean installed(final List<String> toks) {
        List<String> curis;
        if (toks.size() > 1) {
            curis = Arrays.asList(new String[] {toks.get(1)});
        } else {
            if (getNode() == null) {
                return true;
            }
            curis = getNode().getInstalledContributions();
        }
        for (String curi : curis) {
            out.println(curi + " " + getNode().getInstalledContribution(curi).getLocation());
            Contribution c = getNode().getInstalledContribution(curi);
            List<String> deployeds = getNode().getDeployedComposites(curi);
            for (Artifact a : c.getArtifacts()) {
                if (a.getModel() instanceof Composite) {
                    Composite composite = (Composite)a.getModel();
                    String running = deployeds.contains(composite.getURI()) ? "***running***" : "";
                    out.println("   " + composite.getURI() + " " + composite.getName() + " " + running);
                }
            }
        }
        return true;
    }

    boolean listComposites(final String curi) {
        if (getNode() == null) {
            return true;
        }
        Contribution c = getNode().getInstalledContribution(curi);
        for (Artifact a : c.getArtifacts()) {
            if (a.getModel() instanceof Composite) {
                out.println(((Composite)a.getModel()).getName());
            }
        }
        return true;
    }

    boolean load(final String configXmlUrl) throws ContributionReadException, ActivationException, ValidationException {
        Node node = factory.createNodeFromXML(configXmlUrl);
        currentDomain = node.getDomainName();
        nodes.put(currentDomain, node);
        return true;
    }

    boolean printDomainLevelComposite() throws ContributionReadException, ActivationException, ValidationException {
        out.println("TODO");
        // out.println(node.getDomainLevelCompositeAsString());
        return true;
    }

    boolean getQNameDefinition(final String curi, String definintion, String symbolSpace)
        throws ContributionReadException, ActivationException, ValidationException {
        // TODO:
        return true;
    }

    boolean remove(final String curi) throws ContributionReadException, ActivationException, ValidationException {
        if (getNode() == null) {
            out.println("not in domain, use domain command first");
            return true;
        }
        getNode().removeContribution(curi);
        return true;
    }

    boolean run(final String commandsFileURL) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(IOHelper.getLocationAsURL(commandsFileURL).openStream()));
        String l;
        try {
            while ((l = r.readLine()) != null) {
                out.println(l);
                String[] toks = l != null ? l.trim().split(" ") : "".split(" ");
                List<String> toksList = new ArrayList<String>();
                for (String s : toks) {
                    if (s != null && s.trim().length() > 0) {
                        toksList.add(s);
                    }
                }
                apply(eval(toksList));
            }
        } finally {
            r.close();
        }
        return true;
    }

    public boolean stop(List<String> toks) throws ActivationException {
        String curi = toks.get(1);
        if (toks.size() > 2) {
            getNode().removeFromDomainLevelComposite(curi, toks.get(2));
        } else {
            if (standaloneNodes.containsKey(curi)) {
                standaloneNodes.remove(curi).stop();
            } else if (nodes.containsKey(curi)) {
                Node n = nodes.remove(curi);
                n.stop();
                if (n.getDomainName().equals(currentDomain)) {
                    currentDomain = "";
                }
            } else {
                for (String compositeURI : getNode().getDeployedComposites(curi)) {
                    getNode().removeFromDomainLevelComposite(curi, compositeURI);
                }
            }
        }
        return true;
    }

    public boolean bye() {
        for (Node node : nodes.values()) {
            node.stop();
        }
        factory.stop();
        for (Node node : standaloneNodes.values()) {
            node.stop();
        }
        return false;
    }

    boolean start(String curi, String compositeURI) throws ActivationException, ValidationException {
        Contribution c = getNode().getInstalledContribution(curi);
        for (Artifact a : c.getArtifacts()) {
            if (compositeURI.equals(a.getURI())) {
                getNode().addToDomainLevelComposite(curi, compositeURI);
                return true;
            }
        }
        // external composite file ('composite by value')
        try {
            URL url = IOHelper.getLocationAsURL(compositeURI);
            InputStream is = IOHelper.openStream(url);
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            getNode().addDeploymentComposite(curi, br);
        } catch (Exception e) {
            System.out.println(e);
        }

        return true;
    }

    boolean start(String nodeName, String compositeURI, String contributionURL, String... dependentContributionURLs)
        throws ActivationException, ValidationException {
        Node node = NodeFactory.newStandaloneNode(compositeURI, contributionURL, dependentContributionURLs);
        standaloneNodes.put(nodeName, node);
        return true;
    }

    boolean status(final List<String> toks) {
        if (standaloneNodes.size() > 0) {
            out.println("Standalone Nodes:");
            for (String nodeName : standaloneNodes.keySet()) {
                Node node = standaloneNodes.get(nodeName);
                for (String curi : node.getInstalledContributions()) {
                    for (String dc : node.getDeployedComposites(curi)) {
                        out.println("   " + nodeName + " " + dc);
                    }
                }
            }
            out.println();
        }
        if (nodes.size() > 0) {
            for (Node node : nodes.values()) {
                out.println("Domain: " + node.getDomainName());
                List<String> ics;
                if (toks.size() > 1) {
                    ics = new ArrayList<String>();
                    ics.add(toks.get(1));
                } else {
                    ics = node.getInstalledContributions();
                }

                for (String curi : ics) {
                    Contribution c = node.getInstalledContribution(curi);
                    List<String> dcs = node.getDeployedComposites(curi);
                    if (toks.size() > 2) {
                        dcs = new ArrayList<String>();
                        dcs.add(toks.get(2));
                    } else {
                        dcs = node.getDeployedComposites(curi);
                    }
                    for (String compositeUri : dcs) {
                        for (Artifact a : c.getArtifacts()) {
                            if (compositeUri.equals(a.getURI())) {
                                out.println("   " + curi
                                    + " "
                                    + compositeUri
                                    + " "
                                    + ((Composite)a.getModel()).getName());
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    boolean history() {
        for (String l : history)
            out.println(l);
        return true;
    }

    public Node getNode() {
        return nodes.get(currentDomain);
    }

    List<String> read(Object r) throws IOException {
        out.print(currentDomain + "> ");
        final String l;
        if (useJline) {
            l = JLine.readLine(r);
        } else {
            l = ((BufferedReader)r).readLine();
            history.add(l);
        }
        String[] toks = l != null ? l.trim().split(" ") : "bye".split(" ");
        List<String> toksList = new ArrayList<String>();
        for (String s : toks) {
            if (s != null && s.trim().length() > 0) {
                toksList.add(s);
            }
        }
        return toksList;
    }

    Callable<Boolean> eval(final List<String> toks) {
        final String op = toks.size() > 0 ? toks.get(0) : "";

        if (op.equalsIgnoreCase("domain"))
            return new Callable<Boolean>() {
                public Boolean call() throws Exception {
                    return domain(toks.size() > 1 ? toks.get(1) : "");
                }
            };
            if (op.equalsIgnoreCase("domains"))
                return new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                        return domains();
                    }
                };
        if (op.equalsIgnoreCase("install"))
            return new Callable<Boolean>() {
                public Boolean call() throws Exception {
                    return install(toks);
                }
            };
        if (op.equalsIgnoreCase("installed"))
            return new Callable<Boolean>() {
                public Boolean call() throws Exception {
                    return installed(toks);
                }
            };
        if (op.equalsIgnoreCase("load"))
            return new Callable<Boolean>() {
                public Boolean call() throws Exception {
                    return load(toks.get(1));
                }
            };
        if (op.equalsIgnoreCase("printDomainLevelComposite"))
            return new Callable<Boolean>() {
                public Boolean call() throws Exception {
                    return printDomainLevelComposite();
                }
            };
        if (op.equalsIgnoreCase("getQNameDefinition"))
            return new Callable<Boolean>() {
                public Boolean call() throws Exception {
                    return getQNameDefinition(toks.get(1), toks.get(2), toks.get(3));
                }
            };
        if (op.equalsIgnoreCase("remove"))
            return new Callable<Boolean>() {
                public Boolean call() throws Exception {
                    return remove(toks.get(1));
                }
            };
        if (op.equalsIgnoreCase("run"))
            return new Callable<Boolean>() {
                public Boolean call() throws Exception {
                    return run(toks.get(1));
                }
            };
        if (op.equalsIgnoreCase("help"))
            return new Callable<Boolean>() {
                public Boolean call() {
                    return help(toks);
                }
            };
        if (op.equalsIgnoreCase("stop"))
            return new Callable<Boolean>() {
                public Boolean call() throws Exception {
                    return stop(toks);
                }
            };
        if (op.equalsIgnoreCase("bye"))
            return new Callable<Boolean>() {
                public Boolean call() throws Exception {
                    return bye();
                }
            };
        if (op.equalsIgnoreCase("start"))
            return new Callable<Boolean>() {
                public Boolean call() throws Exception {
                    if (currentDomain.length() > 0) {
                        return start(toks.get(1), toks.get(2));
                    } else {
                        String[] duris = null;
                        if (toks.contains("-duris")) {
                            int i = toks.indexOf("-duris");
                            duris = toks.get(i + 1).split(",");
                            toks.remove(i); toks.remove(i+1);
                        }
                        String name = toks.get(1);
                        String contributionURL;
                        String compositeURI;
                        if (toks.size() > 3) {
                            compositeURI = toks.get(2);
                            contributionURL = toks.get(3);
                        } else {
                            compositeURI = null;
                            contributionURL = toks.get(2);
                        }
                        return start(name, compositeURI, contributionURL, duris);
                    }
                }
            };
        if (op.equalsIgnoreCase("status"))
            return new Callable<Boolean>() {
                public Boolean call() {
                    return status(toks);
                }
            };
        if (op.equalsIgnoreCase("history"))
            return new Callable<Boolean>() {
                public Boolean call() {
                    return history();
                }
            };
        if (op.equalsIgnoreCase("") || op.startsWith("#"))
            return new Callable<Boolean>() {
                public Boolean call() {
                    return true;
                }
            };
        return new Callable<Boolean>() {
            public Boolean call() {
                out.println("unknown command");
                return true;
            }
        };
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
        help(null);
        Object reader;
        if (useJline) {
            reader = JLine.createJLineReader(this);
        } else {
            reader = new BufferedReader(new InputStreamReader(in));
        }
        while (apply(eval(read(reader))))
            ;
    }

    boolean help(List<String> toks) {
        String command = (toks == null || toks.size() < 2) ? null : toks.get(1);
        if (command == null) {
            helpOverview();
        } else if ("help".equalsIgnoreCase(command)) {
            helpHelp();
        } else if ("install".equalsIgnoreCase(command)) {
            helpInstall();
        } else if ("installed".equalsIgnoreCase(command)) {
            helpInstalled();
        } else if ("load".equalsIgnoreCase(command)) {
            helpLoad();
        } else if ("remove".equalsIgnoreCase(command)) {
            helpRemove();
        } else if ("run".equalsIgnoreCase(command)) {
            helpRun();
        } else if ("printDomainLevelComposite".equalsIgnoreCase(command)) {
            helpPrintDomainLevelComposite();
        } else if ("start".equalsIgnoreCase(command)) {
            helpStart();
        } else if ("status".equalsIgnoreCase(command)) {
            helpStatus();
        } else if ("stop".equalsIgnoreCase(command)) {
            helpStop();
        } else if ("startup".equalsIgnoreCase(command)) {
            helpStartUp();
        } else if ("bye".equalsIgnoreCase(command)) {
            helpBye();
        }
        return true;
    }

    boolean helpOverview() {
        out.println("Apache Tuscany Shell (" + Version.getVersion()
            + " "
            + Version.getRevsion()
            + " "
            + Version.getBuildTime()
            + ")");
        out.println("Commands:");
        out.println();
        out.println("   help");
        out.println("   domain <domainURI>");
        out.println("   domains");
        out.println("   install [<uri>] <contributionURL> [-start -metadata <url> -duris <uri,uri,...>]");
        out.println("   installed [<contributionURI>]");
        out.println("   load <configXmlURL>");
        out.println("   remove <contributionURI>");
        out.println("   run <commandsFileURL>");
        out.println("   printDomainLevelComposite");
        out.println("   start <curi> <compositeUri>|<contentURL>");
        out.println("   start <name> [<compositeUri>] <contributionURL> [-duris <uri,uri,...>]");
        out.println("   status [<curi> <compositeUri>]");
        out.println("   stop [<curi> <compositeUri>]");
        out.println("   bye");
        out.println();
        if (useJline)
            out.println("Use Tab key for command and argument completion");
        out.println("For detailed help on each command do 'help <command>', for help of startup options do 'help startup'");
        out.println();
        return true;
    }

    void helpHelp() {
        out.println("   help [<command>]");
        out.println();
        out.println("   Outputs help on the Tuscany Shell");
        out.println("   If the command argument is used it provides detailed help on that command otherwise");
        out.println("   it provides an overview of available Shell commands");
        out.println();
        out.println("   To get help on starting the Tuscany Shell use 'help startup'");
        out.println();
        out.println("   Arguments:");
        out.println("      <command> - (optional) the command to get detailed help on");
    }

    void helpDomain() {
        out.println("   domain [<domainURI>]");
        out.println();
        out.println("   Starts or connects to a domain for the given domain URI.");
        out.println("   If no domain URI is specified switch to standalone mode.");
        out.println();
        out.println("   Arguments:");
        out.println("      <domainURI> - (optional) the domain URI of the domain");
    }

    void helpDomains() {
        out.println("   domains");
        out.println();
        out.println("   Shows the currently defined domain URIs");
        out.println();
        out.println("   Arguments:");
        out.println("      none");
    }

    void helpInstall() {
        out.println("   install [<uri>] <contributionURL> [-start -metadata <url> -duris <uri,uri,...>]");
        out.println();
        out.println("   Creates an installed contribution with a supplied root contribution, installed at abase URI.");
        out.println();
        out.println("   Arguments:");
        out.println("      uri - (optional) the URI (name) to use for the contribution. When no uri is specified");
        out.println("               a default URI is used derived from the contribution URL");
        out.println("      contributionURL - (required) the URL to the contribution to install");
        out.println("      -start - (optional) start any composites listed as deployable in the sca-contribution.xml file");
        out.println("      -metadata <url> - (optional) the URL to an external contribution meta data document that should be");
        out.println("               merged into any existing sca-contributions.xml file within the contribution.");
        out.println("      -duris <uri,uri,...> - (optional) specifies the URIs of contributions that are used to resolve the");
        out.println("               dependencies of the root contribution and other dependent contributions.");
        out.println("               When not specified all installed contributions are used to resolve dependencies.");
    }

    void helpInstalled() {
        out.println("   installed [<contributionURI>]");
        out.println();
        out.println("   Shows information about the contributions installed on this node,");
        out.println("   including the contribution URI and location along with the URI");
        out.println("   and QName of any composites within the contribution");
        out.println();
        out.println("   Arguments:");
        out.println("      contributionURI - (optional) the URI of an installed contribution");
    }

    void helpLoad() {
        out.println("   load <configXmlUrl>");
        out.println();
        out.println("   Shows information about the contributions installed on this node,");
        out.println("   including the contribution URI and location along with the URI");
        out.println("   and QName of any composites within the contribution");
        out.println();
        out.println("   Arguments:");
        out.println("      configXmlUrl - (required) the URL of the config file to load");
    }

    void helpRemove() {
        out.println("   remove <contributionURI>");
        out.println();
        out.println("   Removes an installed contribution");
        out.println();
        out.println("   Arguments:");
        out.println("      contributionURI - (required) the URI of an installed contribution");
    }

    void helpRun() {
        out.println("   run <commandsFileURL>");
        out.println();
        out.println("   Runs shell commands stored in file.");
        out.println("   The file should be a text file with one shell command per line. Blank lines and ");
        out.println("   lines starting with # will be ignored.");
        out.println();
        out.println("   Arguments:");
        out.println("      commandsFileURL - (required) the URL of the commands file to run");
    }

    void helpPrintDomainLevelComposite() {
        out.println("   printDomainLevelComposite");
        out.println();
        out.println("   Not yet implemented");
        out.println();
        out.println("   Arguments:");
        out.println("      none");
    }

    void helpStart() {
        out.println("   start <curi> <compositeUri>|<contentURL>");
        out.println("   start <name> [<compositeUri>] <contributionURL> [-duris <uri,uri,...>]");
        out.println();
        out.println("   Starts a composite.");
        out.println("   The composite is added to the domain composite with semantics that correspond to the domain-level");
        out.println("   composite having an <include> statement that references the supplied composite. All of the composites");
        out.println("   components become top-level components and the component services become externally visible");
        out.println("   services (eg. they would be present in a WSDL description of the Domain).");
        out.println();
        out.println("   The second form of the start command starts in standalone mode not part of any SCA domain.");
        out.println();
        out.println("   Arguments (form1):");
        out.println("      curi - (required) the URI of an installed contribution");
        out.println("      compositeUri or contentURL - (required) either the URI of a composite within the contribution");
        out.println("                                              or a URL to an external composite file.");
        out.println("   Arguments (form2):");
        out.println("      name - (required) a name for the started composite/contribution");
        out.println("      compositeUri - (optional) the URI of a composite within the contribution");
        out.println("      contributionURL - (required) the URL to the contribution");
        out.println("      -duris <uri,uri,...> - (optional) specifies the URIs of contributions that are used to resolve the");
        out.println("               dependencies of the root contribution and other dependent contributions.");
    }

    void helpStatus() {
        out.println("   status [<curi> <compositeUri>]");
        out.println();
        out.println("   Shows the status of the Node, listing for each deployed composite its");
        out.println("   contribution URI, the composite URI, and the composite QName.");
        out.println();
        out.println("   Arguments:");
        out.println("      curi - (optional) the URI of an installed contribution");
        out.println("      compositeUri - (optional) the URI of a composite");
    }

    void helpStop() {
        out.println("   stop <curi> [<compositeUri>]");
        out.println("   stop <name>");
        out.println();
        out.println("   Stops a domain or standalone node or individual composites and contributions in a Domain.");
        out.println("   If a composite URI is specified then the composite is removed from the Domain Level composite");
        out.println("   This means that the removal of the components, wires, services and references originally added");
        out.println("   to the domain level composite by the identified composite. If a contribution URI is specified");
        out.println("   without a composite URI then all deployed composites composites in the contribution are stopped.");
        out.println();
        out.println("   Arguments:");
        out.println("      curi - (required) the URI of an installed contribution");
        out.println("      compositeUri - (optional) the URI of a composite");
        out.println("      name - (required) the name of a standalon node or domain to stop");
    }

    void helpBye() {
        out.println("   bye");
        out.println();
        out.println("   All deployed composites are stopped and the Shell exists.");
        out.println();
        out.println("   Arguments:");
        out.println("      none");
    }

    void helpStartUp() {
        out.println("   Tuscany Shell StartUp Options ");
        out.println();
        out.println("   When starting the Tuscany Shell there are optional arguments that can configure the Shell.");
        out.println();
        out.println("   Arguments:");
        out.println("      <domainURI> (optional) the URI of the domain.");
        out.println("                  When the domainURI is a simple string then the Shell starts a standalone");
        out.println("                  Node using the string as the domain name or 'default' if no name is specified.");
        out.println("                  When the domainURI starts with 'uri:' the Shell starts a distributed Node ");
        out.println("                  and the URI can encode parameters to configure the domain as follows:");
        out.println("                  uri:<domainName?key1=value1&key2=value2&...");
        out.println("                  The keys are optional and some keys are:");
        out.println("                  bind=ip[:port] - defines the local bind address and port, if the port is not specified it");
        out.println("                      defaults 14820 and if that port in use it will try incrementing by one till a free port is found.");
        out.println("                  multicast=groupip:port | off - defines if multicast discovery is used and if so what multicast IP group and port is used.");
        out.println("                      It defaults to 224.5.12.10:51482. A value of 'off' means multicast is disabled.");
        out.println("                  wka=ip[:port] - a comma separated list of ip address and port for remote nodes in");
        out.println("                                  the domain group when multicast is not available. The port defaults to 14820.");
        out.println("                  userid= is the userid other nodes must use to connect to this domain group. The default is the default domain name.");
        out.println("                  password= is the password other nodes must use to connect to this domain group. The default is 'tuscany'.");
        out.println();
        out.println("      -nojline    (optional) use plain Java System.in/out instead of JLine");
        out.println("                             (no tab completion or advanced line editing will be available)");
    }
}
