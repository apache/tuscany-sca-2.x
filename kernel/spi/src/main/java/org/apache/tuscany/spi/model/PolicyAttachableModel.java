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
package org.apache.tuscany.spi.model;

import java.util.Collection;

/**
 * Base class for model classes which can be associated with Intent and PolicySet by specifing requires and policySet
 * attributes on xml element.
 */

public abstract class PolicyAttachableModel extends ModelObject implements PolicyAttachable {
    protected Collection<IntentName> requiredIntent;
    protected String policySet;

    public String getPolicySet() {
        return policySet;
    }

    public void setPolicySet(String policySet) {
        this.policySet = policySet;
    }

    public Collection<IntentName> getRequiredIntents() {
        return requiredIntent;
    }

    public void addRequiredIntent(IntentName requiredIntent) {
        this.requiredIntent.add(requiredIntent);
    }


}
