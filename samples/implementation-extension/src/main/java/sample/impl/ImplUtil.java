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

import java.lang.annotation.Annotation;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterfaceContract;

import sample.api.Java;
import sample.api.WSDL;

/**
 * Utility functions to help develop a component implementation extension.
 */
class ImplUtil {

    /**
     * Return a Sample implementation with the given name.
     */
    static SampleImplementation implementation(String name) {
        final SampleImplementation impl = new SampleImplementation(name);
        impl.setUnresolved(true);
        return impl;
    }

    /**
     * Return the Java class configured on an annotation.
     */
    static Class<?> clazz(final Annotation a) {
        return ((Java)a).value();
    }

    /**
     * Return the WSDL QName configured on an annotation.
     */
    static QName qname(final Annotation a) {
        final String uri = ((WSDL)a).value();
        final int h = uri.indexOf('#');
        return new QName(uri.substring(0, h), uri.substring(h + 1));
    }

    /**
     * Convert a Java class to an interface contract.
     */
    static JavaInterfaceContract contract(final Class<?> c, final JavaInterfaceFactory jif) throws InvalidInterfaceException {
        final JavaInterfaceContract ic = jif.createJavaInterfaceContract();
        ic.setInterface(jif.createJavaInterface(c));
        return ic;
    }

    /**
     * Convert a WSDL interface to an interface contract.
     */
    static WSDLInterfaceContract contract(final WSDLInterface wi, final WSDLFactory wf) {
        final WSDLInterfaceContract ic = wf.createWSDLInterfaceContract();
        ic.setInterface(wi);
        return ic;
    }

    /**
     * Convert a Java class to a service.
     */
    static Service service(final Class<?> c, final JavaInterfaceFactory jif, final AssemblyFactory af) throws InvalidInterfaceException {
        Service s = af.createService();
        s.setName(c.getSimpleName());
        s.setInterfaceContract(contract(c, jif));
        return s;
    }

    /**
     * Convert a WSDL interface to a service.
     */
    static Service service(final WSDLInterface wi, final WSDLFactory wf, final AssemblyFactory af) {
        Service s = af.createService();
        s.setName(wi.getName().getLocalPart());
        s.setInterfaceContract(contract(wi, wf));
        return s;
    }

    /**
     * Convert a name and Java class to a reference.
     */
    static Reference reference(final String name, final Class<?> c, final JavaInterfaceFactory jif, final AssemblyFactory af) throws InvalidInterfaceException {
        final Reference r = af.createReference();
        r.setName(name);
        r.setInterfaceContract(contract(c, jif));
        return r;
    }

    /**
     * Convert a name and WSDL interface to a reference.
     */
    static Reference reference(final String name, final WSDLInterface wi, final WSDLFactory wf, final AssemblyFactory af) {
        final Reference r = af.createReference();
        r.setName(name);
        r.setInterfaceContract(contract(wi, wf));
        return r;
    }

    /**
     * Convert a WSDL qname to a WSDL interface.
     */
    static WSDLInterface interfaze(final QName name, final WSDLFactory wif) {
        final WSDLInterface wir = wif.createWSDLInterface();
        wir.setUnresolved(true);
        wir.setName(name);
        return wir;
    }

    /**
     * Convert a WSDL qname to a WSDL definition.
     */
    static WSDLDefinition definition(final QName name, final WSDLFactory wif) {
        final WSDLDefinition wdr = wif.createWSDLDefinition();
        wdr.setUnresolved(true);
        wdr.setNamespace(name.getNamespaceURI());
        wdr.setNameOfPortTypeToResolve(name);
        return wdr;
    }
}
