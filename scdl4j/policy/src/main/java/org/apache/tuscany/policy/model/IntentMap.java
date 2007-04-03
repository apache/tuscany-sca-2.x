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
package org.apache.tuscany.policy.model;

import java.util.List;

/**
 * Represents a policy intent map. See the Policy Framework specification for a
 * description of this element.
 */
public interface IntentMap {

    /**
     * Returns the intent realized by this intent map.
     * 
     * @return the intent realized by this intent map
     */
    Intent getProvidedIntent();

    /**
     * Sets the intent realized by this intent map.
     * 
     * @param providedIntent the intent realized by this intent map
     */
    void setProvidedIntent(Intent providedIntent);

    /**
     * Returns the default qualified intent map.
     * 
     * @return the default qualified intent map
     */
    IntentMap getDefaultQualifiedIntentMap();

    /**
     * Sets the default qualified intent map.
     * 
     * @param defaultQualifiedIntentMap the default qualified intent map
     */
    void setDefaultQualifiedIntentMap(IntentMap defaultQualifiedIntentMap);

    /**
     * Returns the list of children qualified intent maps.
     * 
     * @return
     */
    List<IntentMap> getQualifiedIntentMaps();

    /**
     * Returns the list of concrete policies, either WS-Policy policy
     * attachments, policy references, or policies expressed in another policy
     * language.
     * 
     * @return the list of concrete policies
     */
    List<Object> getPolicies();

    /**
     * Returns true if the model element is unresolved.
     * 
     * @return true if the model element is unresolved.
     */
    boolean isUnresolved();

    /**
     * Sets whether the model element is unresolved.
     * 
     * @param unresolved whether the model element is unresolved
     */
    void setUnresolved(boolean unresolved);

}
