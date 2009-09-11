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

package org.apache.tuscany.sca.policy;

import java.util.List;

/**
 * A qualifier provides a list of policies for a qualified intent within the IntentMap
 */
public interface Qualifier {
    /**
     * Get the qualified intent for this qualifier
     * @return The intent
     */
    Intent getIntent();

    /**
     * Set the qualified intent for this qualifier
     */
    void setIntent(Intent intent);

    /**
     * Get the list of policies provided by this qualifier
     * @return A list of policies
     */
    List<PolicyExpression> getPolicies();
}
