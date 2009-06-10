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

package org.apache.tuscany.sca.core.assembly;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.apache.tuscany.sca.assembly.Endpoint2;
import org.apache.tuscany.sca.assembly.EndpointReference2;

/**
 * A utility to seralize/deserialize Endpoint/EndpointReference objects
 */
public interface EndpointSerializer {
    void readExternal(Endpoint2 endpoint, ObjectInput input) throws IOException;

    void writeExternal(Endpoint2 endpoint, ObjectOutput output) throws IOException;

    void readExternal(EndpointReference2 endpointReference, ObjectInput input) throws IOException;

    void writeExternal(EndpointReference2 endpointReference, ObjectOutput output) throws IOException;
}
