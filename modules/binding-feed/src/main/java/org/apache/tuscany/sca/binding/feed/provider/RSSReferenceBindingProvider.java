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

package org.apache.tuscany.sca.binding.feed.provider;

import org.apache.tuscany.sca.binding.feed.RSSBinding;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;

/**
 * Implementation of the RSS binding provider.
 */
public class RSSReferenceBindingProvider implements ReferenceBindingProvider {

    private RuntimeComponentReference reference;
    private RSSBinding binding;

    public RSSReferenceBindingProvider(RuntimeComponent component,
                                       RuntimeComponentReference reference,
                                       RSSBinding binding) {
        this.reference = reference;
        this.binding = binding;
    }

    public Invoker createInvoker(Operation operation, boolean isCallback) {
        if (isCallback) {
            throw new UnsupportedOperationException();
        } else {
            return new RSSBindingInvoker(binding.getURI(), "rss_2.0");
        }
    }

    public InterfaceContract getBindingInterfaceContract() {
        return reference.getInterfaceContract();
    }

    public void start() {
    }

    public void stop() {
    }

}
