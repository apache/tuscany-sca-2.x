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
package implementation.lifecycle;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.wsdl.PortType;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Service;
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
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLObject;

/**
 * StAX artifact processor for Sample implementations.
 * 
 * @version $Rev$ $Date$
 */
public class LifecycleImplementationProcessor extends BaseStAXArtifactProcessor implements StAXArtifactProcessor<LifecycleImplementation> {
    final AssemblyFactory af;
    final JavaInterfaceFactory jif;
    final WSDLFactory wf;

    public LifecycleImplementationProcessor(final ExtensionPointRegistry ep) {
        final FactoryExtensionPoint fep = ep.getExtensionPoint(FactoryExtensionPoint.class);
        this.af = fep.getFactory(AssemblyFactory.class);
        this.jif = fep.getFactory(JavaInterfaceFactory.class);
        this.wf = fep.getFactory(WSDLFactory.class);
    }

    public QName getArtifactType() {
        return LifecycleImplementation.QN;
    }

    public Class<LifecycleImplementation> getModelType() {
        return LifecycleImplementation.class;
    }

    public LifecycleImplementation read(final XMLStreamReader r, final ProcessorContext ctx) throws ContributionReadException, XMLStreamException {
        // not actually going to read any config for this test
        // so just create a model
        LifecycleImplementation impl = new LifecycleImplementation("helloworld.Helloworld");
        impl.setUnresolved(true);
        return impl;
    }

    public void resolve(final LifecycleImplementation impl, final ModelResolver res, final ProcessorContext ctx) throws ContributionResolveException {
        try {
            Class c = Class.forName("helloworld.Helloworld");
            Service s = af.createService();
            s.setName("Helloworld");
            JavaInterfaceContract ic = jif.createJavaInterfaceContract();
            ic.setInterface(jif.createJavaInterface(c));
            s.setInterfaceContract(ic);
            impl.getServices().add(s);
            impl.setUnresolved(false);
        } catch (Exception ex){
            throw new ContributionResolveException(ex);
        }               
    }

    public void write(final LifecycleImplementation impl, final XMLStreamWriter w, final ProcessorContext ctx) throws ContributionWriteException, XMLStreamException {
        // not required for this test
    }

}
