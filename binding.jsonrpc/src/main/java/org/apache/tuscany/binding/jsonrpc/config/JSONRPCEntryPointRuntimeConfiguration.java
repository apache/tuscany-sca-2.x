/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.binding.jsonrpc.config;

import org.apache.tuscany.core.builder.impl.EntryPointRuntimeConfiguration;
import org.apache.tuscany.core.message.MessageFactory;

/**
 * Creates instances of {@link org.apache.tuscany.core.context.EntryPointContext} configured with the appropriate invocation chains and bindings. This
 * implementation serves as a marker for {@link org.apache.tuscany.binding.jsonrpc.builder.JSONEntryPointWireBuilder}
 * 
 * @version $Rev$ $Date$
 */
public class JSONRPCEntryPointRuntimeConfiguration extends EntryPointRuntimeConfiguration {

    public JSONRPCEntryPointRuntimeConfiguration(String name, String serviceName, MessageFactory messageFactory) {
        super(name, serviceName, messageFactory);
    }

}
