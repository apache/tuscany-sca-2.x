/**
 *
 * Copyright 2006 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.binding.rmi;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.model.DataType;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;

public class RMIReferenceTestCase extends TestCase {

    @SuppressWarnings("unchecked")
    public void testCreateTargetInvoker() {
        // TODO: this doesn't really test anything yet
        RMIReferenceBinding ref = new RMIReferenceBinding(null, null, null, null, null, null);
        ServiceContract contract = new JavaServiceContract();
        contract.setInterfaceClass(String.class);
        List l = new ArrayList();
        l.add(new DataType(null, null));
        DataType in = new DataType(null, l);
        Operation operation = new Operation(null, in, null, null);
        try {
            ref.createTargetInvoker(contract, operation);
        } catch (Exception e) {
            // expected
        }
    }

}
