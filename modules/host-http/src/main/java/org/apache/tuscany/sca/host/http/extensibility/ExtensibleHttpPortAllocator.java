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

package org.apache.tuscany.sca.host.http.extensibility;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.host.http.HttpScheme;

public class ExtensibleHttpPortAllocator implements HttpPortAllocator {
    HttpPortAllocatorExtensionPoint httpPortAllocators;

    public ExtensibleHttpPortAllocator(ExtensionPointRegistry registry) {
        this.httpPortAllocators = registry.getExtensionPoint(HttpPortAllocatorExtensionPoint.class);
    }

    public static ExtensibleHttpPortAllocator getInstance(ExtensionPointRegistry registry) {
        UtilityExtensionPoint utilityExtensionPoint = registry.getExtensionPoint(UtilityExtensionPoint.class);
        return utilityExtensionPoint.getUtility(ExtensibleHttpPortAllocator.class);
    }

    public int getDefaultPort(HttpScheme scheme) {
        if(this.httpPortAllocators.getPortAllocators().isEmpty()) {
            throw new RuntimeException("No port allocators registered");
        }

        return this.httpPortAllocators.getPortAllocators().get(0).getDefaultPort(scheme);
    }
}
