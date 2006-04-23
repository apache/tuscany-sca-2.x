/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
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
package org.apache.tuscany.binding.axis2.builder;

import org.apache.tuscany.binding.axis2.assembly.WebServiceBinding;
import org.apache.tuscany.binding.axis2.config.WebServiceEntryPointContextFactory;
import org.apache.tuscany.core.builder.impl.EntryPointContextFactory;
import org.apache.tuscany.core.extension.EntryPointBuilderSupport;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.model.assembly.Binding;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.osoa.sca.annotations.Scope;


/**
 * Creates a <code>ContextFactory</code> for an entry point configured with the {@link WebServiceBinding}
 *
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class WebServiceEntryPointBuilder extends EntryPointBuilderSupport {

    protected boolean handlesBindingType(Binding binding) {
        return binding instanceof WebServiceBinding;
    }

    protected EntryPointContextFactory createEntryPointContextFactory(EntryPoint entryPoint, MessageFactory msgFactory) {
        return new WebServiceEntryPointContextFactory(entryPoint.getName(),messageFactory);
    }

}
