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

import java.net.URI;
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
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;

/**
 * A few utility functions to help embed a Tuscany runtime, and a simple DSL
 * to help assemble and run SCDL.
 */
public class EmbedUtil {

    /**
     * A runtime embedder context, which conveniently initializes a Node factory
     * and gets the various registries, factories and extension points we need.
     */
    static class Context {
        final NodeFactory nf;
        final ExtensionPointRegistry epr;
        final FactoryExtensionPoint fep;
        final ContributionFactory cf;
        final AssemblyFactory af;
        final JavaInterfaceFactory jif;
        final WSDLFactory wif;
        final URLArtifactProcessorExtensionPoint apep;
        final ExtensibleURLArtifactProcessor aproc;
        final ModelResolverExtensionPoint mrep;
        final ProviderFactoryExtensionPoint pfep;
        
        Context(final NodeFactory nf) {
            this.nf = nf;
            epr = nf.getExtensionPointRegistry();
            fep = epr.getExtensionPoint(FactoryExtensionPoint.class);
            cf = fep.getFactory(ContributionFactory.class);
            af = fep.getFactory(AssemblyFactory.class);
            jif = fep.getFactory(JavaInterfaceFactory.class);
            wif = fep.getFactory(WSDLFactory.class);
            apep = epr.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
            aproc = new ExtensibleURLArtifactProcessor(apep);
            mrep = epr.getExtensionPoint(ModelResolverExtensionPoint.class);
            pfep = epr.getExtensionPoint(ProviderFactoryExtensionPoint.class);
        }
    }
    
    static Context embedContext(NodeFactory nf) {
        return new Context(nf);
    }
    
    /**
     * A mini DSL to help build and assemble contributions and SCDL composites.
     */
    interface Builder<T> {
        T build(Context ec);
    }

    static <T> T build(final Builder<T> builder, final Context ec) {
        return builder.build(ec);
    }

    /**
     * Return a contribution builder.
     */
    static Builder<Contribution> contrib(final String uri, final String loc, final Builder<Artifact>... artifacts) {
        return new Builder<Contribution>() {
            public Contribution build(final Context ec) {
                final Contribution c = ec.cf.createContribution();
                c.setURI(uri);
                c.setLocation(loc);
                c.setModelResolver(new ExtensibleModelResolver(c, ec.mrep, ec.fep));
                for(Builder<Artifact> a: artifacts)
                    c.getArtifacts().add(a.build(ec));
                return c;
            }
        };
    }
    
    /**
     * Return an artifact builder.
     */
    static Builder<Artifact> artifact(final String uri, final Object model) {
        return new Builder<Artifact>() {
            public Artifact build(final Context ec) {
                final Artifact a = ec.cf.createArtifact();
                a.setURI(uri);
                a.setModel(model);
                return a;
            }
        };
    }

    /**
     * Return a composite builder.
     */
    static Builder<Composite> composite(final String ns, final String name, final Builder<Component>... components) {
        return new Builder<Composite>() {
            public Composite build(final Context ec) {
                final Composite compos = ec.af.createComposite();
                compos.setName(new QName(ns, name));
                for(final Builder<Component> c: components)
                    compos.getComponents().add(c.build(ec));
                return compos;
            }
        };
    }

    /**
     * Return a component builder.
     */
    static Builder<Component> component(final String name, final Builder<Implementation> impl, final Builder<ComponentReference>... references) {
        return new Builder<Component>() {
            public Component build(final Context ec) {
                final Component c = ec.af.createComponent();
                c.setName(name);
                c.setImplementation(impl.build(ec));
                for(Builder<ComponentReference> r: references)
                    c.getReferences().add(r.build(ec));
                return c;
            }
        };
    }

    /**
     * Return an implementation builder.
     */
    static Builder<Implementation> implementation(final Class<?> clazz, final Builder<Contract>... contracts) {
        return new Builder<Implementation>() {
            public SampleImplementation build(final Context ec) {
                final SampleImplementation impl = ImplUtil.implementation(clazz.getName());
                impl.clazz = clazz;
                impl.setUnresolved(false);
                for(final Builder<Contract> b: contracts) {
                    Contract c = b.build(ec);
                    if(c instanceof Service)
                        impl.getServices().add((Service)c);
                    else
                        impl.getReferences().add((Reference)c);
                }
                return impl;
            }
        };
    }

    /**
     * Return a reference builder.
     */
    static Builder<Contract> reference(final String name, final Class<?> c) {
        return new Builder<Contract>() {
            public Reference build(final Context ec) {
                try {
                    return ImplUtil.reference(name, c, ec.jif, ec.af);
                } catch(InvalidInterfaceException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    static Builder<Contract> reference(final String name, final WSDLInterface c) {
        return new Builder<Contract>() {
            public Reference build(final Context ec) {
                return ImplUtil.reference(name, c, ec.wif, ec.af);
            }
        };
    }

    static Builder<ComponentReference> reference(final String name, final String target) {
        return new Builder<ComponentReference>() {
            public ComponentReference build(final Context ec) {
                final ComponentReference r = ec.af.createComponentReference();
                r.setName(name);
                final ComponentService s = ec.af.createComponentService();
                s.setUnresolved(true);
                s.setName(target);
                r.getTargets().add(s);
                return r;
            }
        };
    }

    /**
     * Return a service builder.
     */
    static Builder<Contract> service(final Class<?> c) {
        return new Builder<Contract>() {
            public Service build(final Context ec) {
                try {
                    return ImplUtil.service(c, ec.jif, ec.af);
                } catch(InvalidInterfaceException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    static Builder<Contract> service(final WSDLInterface c) {
        return new Builder<Contract>() {
            public Service build(final Context ec) {
                return ImplUtil.service(c, ec.wif, ec.af);
            }
        };
    }

    /**
     * Return a WSDLInterface builder which loads a WSDL into a contribution.
     */
    static Builder<WSDLInterface> wsdli(final String uri, final String ns, final String name, final Contribution c) {
        return new Builder<WSDLInterface>() {
            public WSDLInterface build(final Context ec) {
                try {
                    final ProcessorContext ctx = new ProcessorContext();
                    final WSDLDefinition wd = ec.aproc.read(null, new URI(uri), new URL(new URL(c.getLocation()), uri), ctx, WSDLDefinition.class);
                    c.getModelResolver().addModel(wd, ctx);
                    c.getModelResolver().resolveModel(WSDLDefinition.class, wd, ctx);
                    final WSDLObject<PortType> pt = wd.getWSDLObject(PortType.class, new QName(ns, name));
                    if(pt == null)
                        throw new ContributionResolveException("Couldn't find " + name);
                    final WSDLInterface nwi = ec.wif.createWSDLInterface(pt.getElement(), wd, c.getModelResolver(), null);
                    nwi.setWsdlDefinition(wd);
                    nwi.resetDataBinding(DOMDataBinding.NAME);
                    return nwi;
                } catch(Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
    
    /**
     * Return the extension point registry used by our nodes.
     */
    static ExtensionPointRegistry extensionPoints(final Context ec) {
        return ec.epr;
    }
    
    /**
     * Return the provider factory extension point used by our nodes.
     */
    static ProviderFactoryExtensionPoint providerFactories(final Context ec) {
        return ec.pfep;
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
    static Node node(final NodeFactory nf, final Contribution... contributions) {
        return nf.createNode(Arrays.asList(contributions));
    }

}
