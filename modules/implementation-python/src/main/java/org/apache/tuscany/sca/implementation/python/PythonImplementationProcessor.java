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
package org.apache.tuscany.sca.implementation.python;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static org.apache.tuscany.sca.assembly.Base.SCA11_TUSCANY_NS;

import java.net.URI;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.databinding.json.JSONDataBinding;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.impl.JavaInterfaceImpl;

/**
 * Implements a StAX artifact processor for Python implementations.
 * 
 * @version $Rev$ $Date$
 */
public class PythonImplementationProcessor extends BaseStAXArtifactProcessor implements StAXArtifactProcessor<PythonImplementation> {
    static QName QN = new QName(SCA11_TUSCANY_NS, "implementation.python");

    final InterfaceContract contract;

    public PythonImplementationProcessor(final ExtensionPointRegistry ep) throws InvalidInterfaceException {
        final FactoryExtensionPoint fep = ep.getExtensionPoint(FactoryExtensionPoint.class);
        final JavaInterfaceFactory jf = fep.getFactory(JavaInterfaceFactory.class);
        final JavaInterface eval = jf.createJavaInterface(PythonEval.class);

        class DynamicInterface extends JavaInterfaceImpl {
            DynamicInterface() {
                setJavaClass(eval.getJavaClass());
                setName(eval.getName());
                setRemotable(eval.isRemotable());
                Operation op = eval.getOperations().get(0);
                op.setDynamic(true);
                getOperations().add(op);
                resetDataBinding(JSONDataBinding.NAME);
                setUnresolved(false);
            }

            @Override
            public boolean isDynamic() {
                return true;
            }
        }

        contract = jf.createJavaInterfaceContract();
        contract.setInterface(new DynamicInterface());
    }

    public QName getArtifactType() {
        return QN;
    }

    public Class<PythonImplementation> getModelType() {
        return PythonImplementation.class;
    }

    public PythonImplementation read(final XMLStreamReader r, final ProcessorContext ctx) throws ContributionReadException, XMLStreamException {
        final String scr = r.getAttributeValue(null, "script");
        while(r.hasNext() && !(r.next() == END_ELEMENT && QN.equals(r.getName())))
            ;
        return new PythonImplementation(QN, scr, URI.create(ctx.getContribution().getLocation()).getPath(), contract);
    }

    public void resolve(final PythonImplementation impl, final ModelResolver res, final ProcessorContext ctx) throws ContributionResolveException {
    }

    public void write(final PythonImplementation impl, final XMLStreamWriter w, final ProcessorContext ctx) throws ContributionWriteException, XMLStreamException {
        writeStart(w, QN.getNamespaceURI(), QN.getLocalPart(), new XAttr("script", impl.getScript()));
        writeEnd(w);
    }
}
