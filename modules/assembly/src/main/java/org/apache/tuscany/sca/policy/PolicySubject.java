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
 * For example, a policy subject can be one of the following:
 * <ul>
 * <li>composite
 * <li>component
 * <li>implementation
 * <li>service
 * <li>reference
 * <li>binding
 * <li>interface
 * </ul>
 * @tuscany.spi.extension.asclient
 */
public interface PolicySubject {
    /**
     * Get a list of required intents
     *
     * @return
     */
    List<Intent> getRequiredIntents();

    /**
     * Get a list of attached policySets
     *
     * @return A list of policySets
     */
    List<PolicySet> getPolicySets();

    ExtensionType getExtensionType();
    void setExtensionType(ExtensionType type);
}
