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
package org.apache.tuscany.sca.assembly;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.impl.ComponentTypeImpl;

/**
 * A test component implementation model.
 * 
 * @version $Rev$ $Date$
 */
public class TestImplementation extends ComponentTypeImpl implements Implementation {
    public TestImplementation(AssemblyFactory factory) {

        Property p = factory.createProperty();
        p.setName("currency");
        p.setValue("USD");
        p.setMustSupply(true);
        p.setXSDType(new QName("", ""));
        getProperties().add(p);

        Reference ref1 = factory.createReference();
        ref1.setName("accountDataService");
        ref1.setMultiplicity(Multiplicity.ONE_ONE);
        getReferences().add(ref1);
        ref1.getBindings().add(new TestBinding(factory));

        Reference ref2 = factory.createReference();
        ref2.setName("stockQuoteService");
        ref2.setMultiplicity(Multiplicity.ONE_ONE);
        ref2.setInterfaceContract(new TestInterfaceContract(factory));
        getReferences().add(ref2);
        ref2.getBindings().add(new TestBinding(factory));

        Service s = factory.createService();
        s.setName("AccountService");
        s.setInterfaceContract(new TestInterfaceContract(factory));
        getServices().add(s);
        s.getBindings().add(new TestBinding(factory));

    }

}
