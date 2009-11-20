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
 * Map policies to the qualified intents
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
     * Get a list of qualifiers  
     * 
     * @return A list of qualifiers
     */
    List<Qualifier> getQualifiers();
}
