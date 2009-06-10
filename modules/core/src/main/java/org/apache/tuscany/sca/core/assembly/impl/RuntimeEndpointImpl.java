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

import org.apache.tuscany.sca.assembly.impl.Endpoint2Impl;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.assembly.EndpointSerializer;

/**
 * Runtime model for Endpoint that supports java serialization
 */
public class RuntimeEndpointImpl extends Endpoint2Impl implements Externalizable {
    private static EndpointSerializer serializer;

    /**
     * No-arg constructor for Java serilization
     */
    public RuntimeEndpointImpl() {
        super(null);
    }

    public RuntimeEndpointImpl(ExtensionPointRegistry registry) {
        super(registry);
        if (registry != null) {
            serializer = new EndpointSerializerImpl(registry);
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        // When this method is invoked, the instance is created using the no-arg constructor
        // We need to keep the serializer as a static
        serializer.readExternal(this, in);
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        serializer.writeExternal(this, out);
    }

}
