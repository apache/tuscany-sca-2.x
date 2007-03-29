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

import java.util.ArrayList;
import java.util.Collection;
import static java.util.Collections.unmodifiableCollection;

/**
 * PolicyModel which contains concrete policy content in form of WSPolicyAttachment or other extensions.
 */
public abstract class PolicyContentModel extends PolicyModel {
    /* WSPolicyAttachment contained in this Model */
    protected Collection<WSPolicyAttachment> wsPolicyAttachments = new ArrayList<WSPolicyAttachment>();
    /* Any policy model extensions besides standard intentMap, ws-policyattachment, etc. */
    protected Collection<PolicyModel> extensions = new ArrayList<PolicyModel>();

    public Collection<PolicyModel> getPolicyExtensions() {
        return extensions;
    }

    public Collection<WSPolicyAttachment> getWsPolicyAttachments() {
        return unmodifiableCollection(wsPolicyAttachments);
    }

    public void addWsPolicyAttachment(WSPolicyAttachment attachment) {
        wsPolicyAttachments.add(attachment);
    }
}
