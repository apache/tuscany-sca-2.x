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

package org.apache.tuscany.sca.core.assembly.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.apache.tuscany.sca.assembly.impl.EndpointReferenceImpl;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.runtime.EndpointSerializer;

/**
 * Runtime model for Endpoint that supports java serialization
 */
public class RuntimeEndpointReferenceImpl extends EndpointReferenceImpl implements Externalizable {
    private EndpointSerializer serializer;
    private String xml;

    /**
     * No-arg constructor for Java serilization
     */
    public RuntimeEndpointReferenceImpl() {
        super(null);
    }

    public RuntimeEndpointReferenceImpl(ExtensionPointRegistry registry) {
        super(registry);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.uri = in.readUTF();
        this.xml = in.readUTF();
        // Defer the loading to resolve();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(getURI());
        out.writeUTF(getSerializer().write(this));
    }

    private synchronized EndpointSerializer getSerializer() {
        if (serializer == null) {
            if (registry != null) {
                serializer =
                    registry.getExtensionPoint(UtilityExtensionPoint.class).getUtility(EndpointSerializer.class);
            } else {
                throw new IllegalStateException("No extension registry is set");
            }
        }
        return serializer;
    }

    @Override
    protected void reset() {
        super.reset();
        this.xml = null;
    }

    @Override
    protected void resolve() {
        if (component == null && xml != null) {
            try {
                getSerializer().read(this, xml);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        super.resolve();
    }

    @Override
    public void setExtensionPointRegistry(ExtensionPointRegistry registry) {
        if (this.registry != registry) {
            super.setExtensionPointRegistry(registry);
            serializer = null;
        }
        // resolve();
    }

}
