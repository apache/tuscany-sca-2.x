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
package org.apache.tuscany.container.java.mock.binding.foo;

import org.apache.tuscany.core.builder.impl.EntryPointContextFactory;
import org.apache.tuscany.core.message.MessageFactory;

/**
 * 
 * 
 * @version $Rev$ $Date$
 */
public class FooEntryPointContextFactory extends EntryPointContextFactory {

    public FooEntryPointContextFactory(String name, MessageFactory msgFactory) {
        super(name, msgFactory);
    }

}

