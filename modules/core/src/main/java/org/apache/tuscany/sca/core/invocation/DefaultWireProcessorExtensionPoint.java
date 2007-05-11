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
package org.apache.tuscany.sca.core.invocation;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.core.RuntimeWireProcessor;
import org.apache.tuscany.sca.core.RuntimeWireProcessorExtensionPoint;

/**
 * The default implementation of a <code>WireProcessorExtensionPoint</code>
 *
 * @version $Rev$ $Date$
 */
public class DefaultWireProcessorExtensionPoint implements RuntimeWireProcessorExtensionPoint {

    private final List<RuntimeWireProcessor> processors = new ArrayList<RuntimeWireProcessor>();

    public void addWireProcessor(RuntimeWireProcessor processor) {
        processors.add(processor);
    }

    public void removeWireProcessor(RuntimeWireProcessor processor) {
        processors.remove(processor);
    }
    
    public List<RuntimeWireProcessor> getWireProcessors() {
        return processors;
    }
}
