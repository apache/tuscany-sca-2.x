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
package org.apache.tuscany.model.assembly.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.Reference;

/**
 */
public class ConfiguredReferenceImpl extends ConfiguredPortImpl implements ConfiguredReference {

    private List<ConfiguredService> configuredServices = new ArrayList<ConfiguredService>();

    /**
     * Constructor
     */
    protected ConfiguredReferenceImpl() {
    }

    /**
     * @see org.apache.tuscany.model.assembly.ConfiguredReference#getReference()
     */
    public Reference getReference() {
        return (Reference) super.getPort();
    }

    /**
     * @see org.apache.tuscany.model.assembly.ConfiguredReference#getConfiguredServices()
     */
    public List<ConfiguredService> getConfiguredServices() {
        return configuredServices;
    }

}
