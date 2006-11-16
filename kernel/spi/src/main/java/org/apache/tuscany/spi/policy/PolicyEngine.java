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
package org.apache.tuscany.spi.policy;

import java.util.Collection;
import javax.xml.namespace.QName;

import org.apache.tuscany.spi.model.IntentName;
import org.apache.tuscany.spi.model.PolicyModel;

/**
 * Responsible for matching concrete policy artifacts required by abstract intent name.
 */
public interface PolicyEngine {

    /**
     * Retrieve policies which match the intents requirement on a SCA artifact. See SCA policy frame spec. 1.4.5 Guided
     * Selection of PolicySets using Intents
     *
     * @param requires      Intent names requred by the SCA artifact
     * @param policySetName PolicySet names specify on the SCA artifact explicitly
     * @param artifactType  QName of SCA artifact type, which may be a abstract artifact type
     * @return Collection contains all policy matching the intent requirement
     */
    Collection<PolicyModel> getPolicy(IntentName[] requires, QName[] policySetName, QName artifactType);
}
