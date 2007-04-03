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

package org.apache.tuscany.services.spi.contribution.extension;

import org.apache.tuscany.services.spi.contribution.ContributionProcessor;
import org.apache.tuscany.services.spi.contribution.ContributionProcessorRegistry;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

/**
 * The base class for ContributionProcessor implementations
 *
 * @version $Rev$ $Date$
 */
@EagerInit
@Service(ContributionProcessor.class)
public abstract class ContributionProcessorExtension implements ContributionProcessor {
    /**
     * The ContributionProcessorRegistry that this processor should register with; usually set by injection. This
     * registry may also be used to process other sub-artifacts.
     */
    protected ContributionProcessorRegistry registry;

    /**
     * @param registry the registry to set
     */
    @Reference
    public void setContributionProcessorRegistry(ContributionProcessorRegistry registry) {
        this.registry = registry;
    }

    /**
     * Initialize the processor. It registers itself to the registry by content type it supports.
     */
    @Init
    public void start() {
        registry.register(this.getContentType(), this);
    }

    /**
     * Destroy the processor. It unregisters itself from the registry.
     */
    @Destroy
    public void stop() {
        registry.unregister(this.getContentType());
    }

    /**
     * Returns the content type that this implementation can handle.
     *
     * @return the content type that this implementation can handle
     */
    public abstract String getContentType();

}
