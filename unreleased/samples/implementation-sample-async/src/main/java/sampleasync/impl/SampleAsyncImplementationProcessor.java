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
package sampleasync.impl;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static sampleasync.impl.SampleAsyncImplementation.QN;
import static sampleasync.impl.ImplUtil.clazz;
import static sampleasync.impl.ImplUtil.definition;
import static sampleasync.impl.ImplUtil.implementation;
import static sampleasync.impl.ImplUtil.interfaze;
import static sampleasync.impl.ImplUtil.qname;
import static sampleasync.impl.ImplUtil.reference;
import static sampleasync.impl.ImplUtil.service;

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
public class SampleAsyncImplementationProcessor extends BaseStAXArtifactProcessor implements StAXArtifactProcessor<SampleAsyncImplementation> {
    final AssemblyFactory af;
    final JavaInterfaceFactory jif;
    final WSDLFactory wf;

    public SampleAsyncImplementationProcessor(final ExtensionPointRegistry ep) {
        final FactoryExtensionPoint fep = ep.getExtensionPoint(FactoryExtensionPoint.class);
        this.af = fep.getFactory(AssemblyFactory.class);
        this.jif = fep.getFactory(JavaInterfaceFactory.class);
        this.wf = fep.getFactory(WSDLFactory.class);
    }

    public QName getArtifactType() {
        return QN;
    }

    public Class<SampleAsyncImplementation> getModelType() {
        return SampleAsyncImplementation.class;
    }

    public SampleAsyncImplementation read(final XMLStreamReader r, final ProcessorContext ctx) throws ContributionReadException, XMLStreamException {
        // Read the component implementation element
        final SampleAsyncImplementation impl = implementation(r.getAttributeValue(null, "class"));
        while(r.hasNext() && !(r.next() == END_ELEMENT && QN.equals(r.getName())))
            ;
        return impl;
    }

    public void resolve(final SampleAsyncImplementation impl, final ModelResolver res, final ProcessorContext ctx) throws ContributionResolveException {
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

    public void write(final SampleAsyncImplementation impl, final XMLStreamWriter w, final ProcessorContext ctx) throws ContributionWriteException, XMLStreamException {
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
            return domBound(wi);

        final WSDLDefinition wd = res.resolveModel(WSDLDefinition.class, definition(wi.getName(), wif), ctx);
        if(wd.isUnresolved())
            throw new ContributionResolveException("Couldn't find " + name.getNamespaceURI());

        WSDLObject<PortType> pt = wd.getWSDLObject(PortType.class, name);
        if(pt == null)
            throw new ContributionResolveException("Couldn't find " + name);
        try {
            final WSDLInterface nwi = wif.createWSDLInterface(pt.getElement(), wd, res, ctx.getMonitor());
            nwi.setWsdlDefinition(wd);
            res.addModel(nwi, ctx);
            return domBound(nwi);
        } catch(InvalidInterfaceException e) {
            throw new ContributionResolveException(e);
        }
    }

    /**
     * Return a WSDL interface configured to use a DOM databinding. 
     */
    static WSDLInterface domBound(WSDLInterface wi) throws ContributionResolveException {
        try {
            final WSDLInterface domwi = (WSDLInterface)wi.clone();
            domwi.resetDataBinding(DOMDataBinding.NAME);
            return domwi;
        } catch(CloneNotSupportedException e) {
            throw new ContributionResolveException(e);
        }
    }
}
