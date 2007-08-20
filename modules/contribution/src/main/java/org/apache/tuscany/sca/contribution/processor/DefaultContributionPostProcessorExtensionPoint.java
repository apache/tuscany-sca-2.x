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

package org.apache.tuscany.sca.contribution.processor;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of ContributionPostProcessor Extension Point
 * @deprecated Please use DefaultContributionListenerExtensionPoint instead
 * 
 * @version $Rev$ $Date$
 */
@Deprecated
public class DefaultContributionPostProcessorExtensionPoint implements ContributionPostProcessorExtensionPoint {

    /**
     * Processor registry
     */
    private List <ContributionPostProcessor> registry = new ArrayList<ContributionPostProcessor>();

    public DefaultContributionPostProcessorExtensionPoint() {
    }

    public void addPostProcessor(ContributionPostProcessor postProcessor) {
        this.registry.add(postProcessor);
        
    }

    public void removePostProcessor(ContributionPostProcessor postProcessor) {
        this.registry.remove(postProcessor);
    }

    public List<ContributionPostProcessor> getPostProcessors() {
        return registry;
    }
    
}
