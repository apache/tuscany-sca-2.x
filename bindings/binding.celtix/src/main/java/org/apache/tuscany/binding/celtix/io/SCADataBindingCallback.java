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
package org.apache.tuscany.binding.celtix.io;

import org.w3c.dom.Node;

import commonj.sdo.helper.TypeHelper;
import org.objectweb.celtix.bindings.DataReader;
import org.objectweb.celtix.bindings.DataWriter;
import org.objectweb.celtix.bus.bindings.AbstractWSDLOperationDataBindingCallback;
import org.objectweb.celtix.bus.bindings.WSDLOperationInfo;
import org.objectweb.celtix.context.ObjectMessageContext;


/**
 * @version $Rev$ $Date$
 */
public class SCADataBindingCallback extends AbstractWSDLOperationDataBindingCallback {

    protected boolean hasInOut;
    protected TypeHelper typeHelper;

    public SCADataBindingCallback(WSDLOperationInfo op, boolean inout, TypeHelper theTypeHelper) {
        super(op);
        this.hasInOut = inout;
        this.typeHelper = theTypeHelper;
    }

    public boolean hasInOut() {
        return hasInOut;
    }

    public Mode getMode() {
        return Mode.PARTS;
    }

    public Class<?>[] getSupportedFormats() {
        return new Class<?>[]{Node.class};
    }

    public TypeHelper getTypeHelper() {
        return typeHelper;
    }

    @SuppressWarnings("unchecked")
    public <T> DataWriter<T> createWriter(Class<T> cls) {
        if (cls == Node.class) {
            return (DataWriter<T>) new NodeDataWriter(this);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> DataReader<T> createReader(Class<T> cls) {
        if (cls == Node.class) {
            return (DataReader<T>) new NodeDataReader(this);
        }
        //REVISIT - need to figure out what to do with Faults
        return null;
    }

    public void initObjectContext(ObjectMessageContext octx) {
        //REVISIT - is this even used?
    }


}
