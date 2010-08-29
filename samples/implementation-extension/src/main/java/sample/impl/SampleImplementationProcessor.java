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

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static sample.impl.SampleImplementation.QN;
import static sample.impl.SampleUtil.clazz;
import static sample.impl.SampleUtil.definition;
import static sample.impl.SampleUtil.implementation;
import static sample.impl.SampleUtil.interfaze;
import static sample.impl.SampleUtil.qname;
import static sample.impl.SampleUtil.reference;
import static sample.impl.SampleUtil.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.wsdl.PortType;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ClassReference;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.databinding.xml.DOMDataBinding;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLObject;

import sample.api.Java;
import sample.api.WSDL;

/**
 * StAX artifact processor for Sample implementations.
 * 
 * @version $Rev$ $Date$
 */
public class SampleImplementationProcessor extends BaseStAXArtifactProcessor implements StAXArtifactProcessor<SampleImplementation> {
    final AssemblyFactory af;
    final JavaInterfaceFactory jif;
    final WSDLFactory wf;

    public SampleImplementationProcessor(final ExtensionPointRegistry ep) {
        final FactoryExtensionPoint fep = ep.getExtensionPoint(FactoryExtensionPoint.class);
        this.af = fep.getFactory(AssemblyFactory.class);
        this.jif = fep.getFactory(JavaInterfaceFactory.class);
        this.wf = fep.getFactory(WSDLFactory.class);
    }

    public QName getArtifactType() {
        return QN;
    }

    public Class<SampleImplementation> getModelType() {
        return SampleImplementation.class;
    }

    public SampleImplementation read(final XMLStreamReader r, final ProcessorContext ctx) throws ContributionReadException, XMLStreamException {
        // Read the component implementation element
        final SampleImplementation impl = implementation(r.getAttributeValue(null, "class"));
        while(r.hasNext() && !(r.next() == END_ELEMENT && QN.equals(r.getName())))
            ;
        return impl;
    }

    public void resolve(final SampleImplementation impl, final ModelResolver res, final ProcessorContext ctx) throws ContributionResolveException {
        try {
            // Resolve and introspect the implementation class
            impl.clazz = resolve(impl.name, res, ctx);

            for(final Annotation a: impl.clazz.getAnnotations()) {
                if(a instanceof Java)
                    impl.getServices().add(service(clazz(a), jif, af));
                else if(a instanceof WSDL)
                    impl.getServices().add(service(resolve(qname(a), res, ctx, wf), wf, af));
            }

            for(Field f: impl.clazz.getDeclaredFields()) {
                for(final Annotation a: f.getAnnotations()) {
                    if(a instanceof Java)
                        impl.getReferences().add(reference(f.getName(), clazz(a), jif, af));
                    else if(a instanceof WSDL)
                        impl.getReferences().add(reference(f.getName(), resolve(qname(a), res, ctx, wf), wf, af));
                }
            }

            impl.setUnresolved(false);
        } catch(InvalidInterfaceException e) {
            throw new ContributionResolveException(e);
        }
    }

    public void write(final SampleImplementation impl, final XMLStreamWriter w, final ProcessorContext ctx) throws ContributionWriteException, XMLStreamException {
        writeStart(w, QN.getNamespaceURI(), QN.getLocalPart(), new XAttr("class", impl.name));
        writeEnd(w);
    }

    /**
     * Resolve a Java class.
     */
    static Class<?> resolve(final String name, final ModelResolver res, final ProcessorContext ctx) throws ContributionResolveException {
        final ClassReference cr = res.resolveModel(ClassReference.class, new ClassReference(name), ctx);
        if(cr.getJavaClass() != null)
            return cr.getJavaClass();
        throw new ContributionResolveException(new ClassNotFoundException(name));
    }

    /**
     * Resolve a WSDL interface.
     */
    static WSDLInterface resolve(final QName name, final ModelResolver res, final ProcessorContext ctx, final WSDLFactory wif) throws ContributionResolveException {
        final WSDLInterface wi = res.resolveModel(WSDLInterface.class, interfaze(name, wif), ctx);
        if(!wi.isUnresolved())
            return wi;

        final WSDLDefinition wd = res.resolveModel(WSDLDefinition.class, definition(wi.getName(), wif), ctx);
        if(wd.isUnresolved())
            throw new ContributionResolveException("Couldn't find " + name.getNamespaceURI());

        WSDLObject<PortType> pt = wd.getWSDLObject(PortType.class, name);
        if(pt == null)
            throw new ContributionResolveException("Couldn't find " + name);
        try {
            final WSDLInterface nwi = wif.createWSDLInterface(pt.getElement(), wd, res, ctx.getMonitor());
            nwi.setWsdlDefinition(wd);
            nwi.resetDataBinding(DOMDataBinding.NAME);
            res.addModel(nwi, ctx);
            return nwi;
        } catch(InvalidInterfaceException e) {
            throw new ContributionResolveException(e);
        }
    }
}
