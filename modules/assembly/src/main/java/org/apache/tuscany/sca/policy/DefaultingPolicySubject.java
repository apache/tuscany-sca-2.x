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
 * A policy subject is an entity in the assembly with which a policy can be
 * associated.
 * 
 * This default extension provides a mechanism of associating default intents
 * with a policy subject so that the framework will take default intents 
 * into account if no intents are specified by the user. 
 * 
 * Default intents are mainly applicable when an artifact defines mayProvides
 * intents but the user specified no intents
 */
public interface DefaultingPolicySubject extends PolicySubject {
    
    /**
     * Default intents are mainly applicable when an
     * artifact defines mayProvides intents but the 
     * user specified no intents. In some cases the
     * artifact will implement a default intent so 
     * this collection provides the information for the
     * framework to determine what those defaults are
     * 
     * @return A list of default intent records
     */
    List<DefaultIntent> getDefaultIntents();
}
