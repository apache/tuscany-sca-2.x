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
package org.apache.tuscany.sca.runtime;

import java.util.ArrayList;
import java.util.List;


/**
 * The default implementation of a <code>WireProcessorExtensionPoint</code>
 *
 * @version $Rev$ $Date$
 */
public class DefaultWireProcessorExtensionPoint implements RuntimeWireProcessorExtensionPoint {

	/**
	 * The list of WireProcessors available to the runtime
	 */
    private final List<RuntimeWireProcessor> processors = new ArrayList<RuntimeWireProcessor>();

    /**
    * Registers a wire-processor in the runtime
    * 
    * @param processor The processor to register
    */
    public void addWireProcessor(RuntimeWireProcessor processor) {
        processors.add(processor);
    }

    /**
     * De-registers a wire-processor in the runtime
     * 
     * @param processor The processor to de-register
     */
    public void removeWireProcessor(RuntimeWireProcessor processor) {
        processors.remove(processor);
    }
    
    /**
     * Returns a list of registered wire-processors.
     * 
     * @return The list of wire processors
     */    
    public List<RuntimeWireProcessor> getWireProcessors() {
        return processors;
    }
}
