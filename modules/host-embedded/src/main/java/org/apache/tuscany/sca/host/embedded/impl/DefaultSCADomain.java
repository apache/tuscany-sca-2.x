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

package org.apache.tuscany.sca.host.embedded.impl;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.core.assembly.ActivationException;
import org.apache.tuscany.sca.core.assembly.CompositeActivator;
import org.apache.tuscany.sca.core.assembly.RuntimeComponentImpl;
import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.apache.tuscany.sca.host.embedded.management.ComponentListener;
import org.apache.tuscany.sca.host.embedded.management.ComponentManager;
import org.apache.tuscany.sca.node.SCAClient;
import org.apache.tuscany.sca.node.SCAContribution;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;
import org.apache.tuscany.sca.node.impl.NodeImpl;
import org.osoa.sca.CallableReference;
import org.osoa.sca.ServiceReference;

/**
 * A default SCA domain facade implementation.
 * 
 * @version $Rev$ $Date$
 */
public class DefaultSCADomain extends SCADomain {

    private String uri;
    private String[] composites;
    // private Composite domainComposite;
    // private List<Contribution> contributions;
    private Map<String, Component> components;
    private ComponentManager componentManager;
    // private ClassLoader runtimeClassLoader;
    private ClassLoader applicationClassLoader;
    private String domainURI;
    private List<String> contributionURLs;

    private CompositeActivator compositeActivator;
    private SCANode node;
    private SCAClient client;

    /**
     * Constructs a new domain facade.
     * 
     * @param domainURI
     * @param contributionLocation
     * @param composites
     */
    public DefaultSCADomain(ClassLoader runtimeClassLoader,
                            ClassLoader applicationClassLoader,
                            String domainURI,
                            String contributionLocation,
                            String... composites) {
        this.uri = domainURI;
        this.composites = composites;
        // this.runtimeClassLoader = runtimeClassLoader;
        this.applicationClassLoader = applicationClassLoader;
        this.domainURI = domainURI;
        this.contributionURLs = new ArrayList<String>();
        if (contributionLocation != null && !"/".equals(contributionLocation)) {
            this.contributionURLs.add(contributionLocation);
        }
        this.composites = composites;

        init();

        this.componentManager = new DefaultSCADomainComponentManager(this);

    }

    /**
     * A hack to create an aggregated composite
     * @param classLoader
     * @param composites
     * @return
     */
    private String createDeploymentComposite(ClassLoader classLoader, String composites[]) {
        try {
            StringBuffer xml =
                new StringBuffer("<sca:composite xmlns:sca=\"http://www.osoa.org/xmlns/sca/1.0\"")
                    .append(" targetNamespace=\"http://tempuri.org\" name=\"aggregated\">\n");
            XMLInputFactory factory = XMLInputFactory.newInstance();
            for (int i = 0; i < composites.length; i++) {
                URL url = classLoader.getResource(composites[i]);
                if (url == null) {
                    continue;
                }
                String location = NodeImpl.getContributionURL(url, composites[i]).toString();
                if (!contributionURLs.contains(location)) {
                    contributionURLs.add(location);
                }
                URLConnection connection = url.openConnection();
                connection.setUseCaches(false);
                XMLStreamReader reader = factory.createXMLStreamReader(connection.getInputStream());
                reader.nextTag();

                assert Constants.COMPOSITE_QNAME.equals(reader.getName());
                String ns = reader.getAttributeValue(null, "targetNamespace");
                if (ns == null) {
                    ns = XMLConstants.NULL_NS_URI;
                }
                String name = reader.getAttributeValue(null, "name");
                reader.close();
                if (XMLConstants.NULL_NS_URI.equals(ns)) {
                    xml.append("<sca:include name=\"").append(name).append("\"/>\n");
                } else {
                    xml.append("<sca:include xmlns:ns").append(i).append("=\"").append(ns).append("\"");
                    xml.append(" name=\"").append("ns").append(i).append(":").append(name).append("\"/>\n");
                }
            }
            xml.append("</sca:composite>");
            // System.out.println(xml.toString());
            return xml.toString();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void init() {
        SCANodeFactory factory = SCANodeFactory.newInstance();

        List<SCAContribution> contributions = new ArrayList<SCAContribution>();

        if (composites != null && composites.length > 1) {
            // Create an aggregated composite that includes all the composites as Node API only takes one composite
            String content = createDeploymentComposite(applicationClassLoader, composites);
            // Create SCA contributions
            for (String location : contributionURLs) {
                contributions.add(new SCAContribution(location, location));
            }
            node =
                factory.createSCANode("http://tempuri.org/aggregated", content, contributions
                    .toArray(new SCAContribution[contributions.size()]));
        } else {
            for (String location : contributionURLs) {
                contributions.add(new SCAContribution(location, location));
            }
            String composite = (composites != null && composites.length >= 1) ? composites[0] : null;
            if (!contributions.isEmpty()) {
                node =
                    factory.createSCANode(composite, contributions.toArray(new SCAContribution[contributions.size()]));
            } else {
                node = factory.createSCANodeFromClassLoader(composite, applicationClassLoader);
            }
        }
        client = (SCAClient)node;
        compositeActivator = ((NodeImpl)node).getCompositeActivator();
        components = new HashMap<String, Component>();

        node.start();

        getComponents(compositeActivator.getDomainComposite());
    }

    private void getComponents(Composite composite) {
        for (Component c : composite.getComponents()) {
            components.put(c.getName(), c);
        }
        for (Composite cp : composite.getIncludes()) {
            getComponents(cp);
        }
    }

    @Override
    public void close() {
        super.close();
        node.stop();

    }

    @Override
    @SuppressWarnings("unchecked")
    public <B, R extends CallableReference<B>> R cast(B target) throws IllegalArgumentException {
        return (R)client.cast(target);
    }

    @Override
    public <B> B getService(Class<B> businessInterface, String serviceName) {
        return client.getService(businessInterface, serviceName);
    }

    @Override
    public <B> ServiceReference<B> getServiceReference(Class<B> businessInterface, String name) {
        return client.getServiceReference(businessInterface, name);
    }

    @Override
    public String getURI() {
        return uri;
    }

    @Override
    public ComponentManager getComponentManager() {
        return componentManager;
    }

    public Set<String> getComponentNames() {
        return components.keySet();
        /*
        Set<String> componentNames = new HashSet<String>();
        for (Contribution contribution : contributions) {
            for (Artifact artifact : contribution.getArtifacts()) {
                if (artifact.getModel() instanceof Composite) {
                    for (Component component : ((Composite)artifact.getModel()).getComponents()) {
                        componentNames.add(component.getName());
                    }
                }
            }
        }
        return componentNames;
        */
    }

    public Component getComponent(String componentName) {
        return components.get(componentName);
        /*
        for (Contribution contribution : contributions) {
            for (Artifact artifact : contribution.getArtifacts()) {
                if (artifact.getModel() instanceof Composite) {
                    for (Component component : ((Composite)artifact.getModel()).getComponents()) {
                        if (component.getName().equals(componentName)) {
                            return component;
                        }
                    }
                }
            }
        }
        return null;
        */
    }

    public void startComponent(String componentName) throws ActivationException {
        Component component = getComponent(componentName);
        if (component == null) {
            throw new IllegalArgumentException("no component: " + componentName);
        }
        compositeActivator.start(component);
    }

    public void stopComponent(String componentName) throws ActivationException {
        Component component = getComponent(componentName);
        if (component == null) {
            throw new IllegalArgumentException("no component: " + componentName);
        }
        compositeActivator.stop(component);
    }
}

class DefaultSCADomainComponentManager implements ComponentManager {

    protected DefaultSCADomain scaDomain;
    protected List<ComponentListener> listeners = new CopyOnWriteArrayList<ComponentListener>();

    public DefaultSCADomainComponentManager(DefaultSCADomain scaDomain) {
        this.scaDomain = scaDomain;
    }

    public void addComponentListener(ComponentListener listener) {
        this.listeners.add(listener);
    }

    public void removeComponentListener(ComponentListener listener) {
        this.listeners.remove(listener);
    }

    public Set<String> getComponentNames() {
        return scaDomain.getComponentNames();
    }

    public Component getComponent(String componentName) {
        return scaDomain.getComponent(componentName);
    }

    public void startComponent(String componentName) throws ActivationException {
        scaDomain.startComponent(componentName);
    }

    public void stopComponent(String componentName) throws ActivationException {
        scaDomain.stopComponent(componentName);
    }

    public void notifyComponentStarted(String componentName) {
        for (ComponentListener listener : listeners) {
            try {
                listener.componentStarted(componentName);
            } catch (Exception e) {
                e.printStackTrace(); // TODO: log
            }
        }
    }

    public void notifyComponentStopped(String componentName) {
        for (ComponentListener listener : listeners) {
            try {
                listener.componentStopped(componentName);
            } catch (Exception e) {
                e.printStackTrace(); // TODO: log
            }
        }
    }

    public boolean isComponentStarted(String componentName) {
        RuntimeComponentImpl runtimeComponent = (RuntimeComponentImpl)getComponent(componentName);
        return runtimeComponent.isStarted();
    }

}
