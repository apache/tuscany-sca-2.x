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

package org.apache.tuscany.sca.node.spi;

import java.util.List;

import org.osoa.sca.annotations.Remotable;

/**
 * A service interface for managing the components in a node
 * 
 * @version $Rev: 552343 $ $Date: 2007-09-07 12:41:52 +0100 (Fri, 07 Sep 2007) $
 */
@Remotable
public interface ComponentManagerService {

    /**
     * Get a list of component info. On for each component in the node
     * 
     * @return
     */
    List<ComponentInfo> getComponentInfos();

    /**
     * The info for a named component
     * 
     * @param componentName
     * @return
     */
    ComponentInfo getComponentInfo(String componentName);



}
