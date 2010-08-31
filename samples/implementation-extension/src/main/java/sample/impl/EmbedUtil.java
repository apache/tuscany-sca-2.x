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

package sample.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.processor.ContributionException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ExtensibleURLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ExtensibleModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.databinding.xml.DOMDataBinding;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLObject;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.configuration.NodeConfiguration;
import org.apache.tuscany.sca.node.configuration.impl.NodeConfigurationImpl;
import org.apache.tuscany.sca.node.impl.NodeFactoryImpl;
import org.apache.tuscany.sca.node.impl.NodeImpl;

/**
 * Simple DSL functions to help assemble and run SCDL.
 */
public class EmbedUtil {

    static final NodeFactoryImpl nf;
    static final ExtensionPointRegistry epr;
    static final FactoryExtensionPoint fep;
    static final ContributionFactory cf;
    static final AssemblyFactory af;
    static final JavaInterfaceFactory jif;
    static final WSDLFactory wif;
    static final URLArtifactProcessorExtensionPoint apep;
    static final ExtensibleURLArtifactProcessor aproc;
    static final ModelResolverExtensionPoint mrep;
    static {
        nf = new NodeFactoryImpl();
        epr = nf.getExtensionPointRegistry();
        fep = epr.getExtensionPoint(FactoryExtensionPoint.class);
        cf = fep.getFactory(ContributionFactory.class);
        af = fep.getFactory(AssemblyFactory.class);
        jif = fep.getFactory(JavaInterfaceFactory.class);
        wif = fep.getFactory(WSDLFactory.class);
        apep = epr.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
        aproc = new ExtensibleURLArtifactProcessor(apep);
        mrep = epr.getExtensionPoint(ModelResolverExtensionPoint.class);
    }

    static Contribution contrib(final String uri, final String loc, final Artifact... artifacts) {
        final Contribution c = cf.createContribution();
        c.setURI(uri);
        c.setLocation(loc);
        c.setModelResolver(new ExtensibleModelResolver(c, mrep, fep));
        for(Artifact a: artifacts)
            c.getArtifacts().add(a);
        return c;
    }

    static Artifact artifact(final String uri, final Object model) {
        final Artifact a = cf.createArtifact();
        a.setURI(uri);
        a.setModel(model);
        return a;
    }

    static Composite composite(final String ns, final String name, final Component... components) {
        final Composite compos = af.createComposite();
        compos.setName(new QName(ns, name));
        for(final Component c: components)
            compos.getComponents().add(c);
        return compos;
    }

    static Component component(final String name, final Implementation impl, final ComponentReference... references) {
        final Component c = af.createComponent();
        c.setName(name);
        c.setImplementation(impl);
        for(ComponentReference r: references)
            c.getReferences().add(r);
        return c;
    }

    static SampleImplementation implementation(final Class<?> clazz, final Contract... contracts) {
        final SampleImplementation impl = ImplUtil.implementation(clazz.getName());
        impl.clazz = clazz;
        impl.setUnresolved(false);
        for(final Contract c: contracts) {
            if(c instanceof Service)
                impl.getServices().add((Service)c);
            else
                impl.getReferences().add((Reference)c);
        }
        return impl;
    }

    static WSDLInterface wsdli(final String uri, final String ns, final String name, final Contribution c) throws InvalidInterfaceException, ContributionException, IOException, URISyntaxException {
        final ProcessorContext ctx = new ProcessorContext();
        final WSDLDefinition wd = aproc.read(null, new URI(uri), new URL(new URL(c.getLocation()), uri), ctx, WSDLDefinition.class);
        c.getModelResolver().addModel(wd, ctx);
        c.getModelResolver().resolveModel(WSDLDefinition.class, wd, ctx);
        final WSDLObject<PortType> pt = wd.getWSDLObject(PortType.class, new QName(ns, name));
        if(pt == null)
            throw new ContributionResolveException("Couldn't find " + name);
        final WSDLInterface nwi = wif.createWSDLInterface(pt.getElement(), wd, c.getModelResolver(), null);
        nwi.setWsdlDefinition(wd);
        nwi.resetDataBinding(DOMDataBinding.NAME);
        return nwi;
    }

    static Reference reference(final String name, final Class<?> c) throws InvalidInterfaceException {
        return ImplUtil.reference(name, c, jif, af);
    }

    static Reference reference(final String name, final WSDLInterface c) {
        return ImplUtil.reference(name, c, wif, af);
    }

    static ComponentReference reference(final String name, final String target) {
        final ComponentReference r = af.createComponentReference();
        r.setName(name);
        final ComponentService s = af.createComponentService();
        s.setUnresolved(true);
        s.setName(target);
        r.getTargets().add(s);
        return r;
    }

    static Service service(final Class<?> c) throws InvalidInterfaceException {
        return ImplUtil.service(c, jif, af);
    }

    static Service service(final WSDLInterface c) {
        return ImplUtil.service(c, wif, af);
    }

    /**
     * Add a deployable composite to a contribution.
     */
    static Contribution deploy(final Contribution contrib, final Composite... comps) {
        for(Composite c: comps)
            contrib.getDeployables().add(c);
        return contrib;
    }

    /**
     * Configure a node with a list of contributions.
     */
    static Node node(final String uri, final Contribution... contributions) {
        final NodeConfiguration cfg = new NodeConfigurationImpl();
        cfg.setURI(uri);
        return new NodeImpl(nf, cfg, Arrays.asList(contributions));
    }

}
