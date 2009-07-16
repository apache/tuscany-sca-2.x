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

package org.apache.tuscany.sca.dosgi.discovery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.osgi.service.discovery.DiscoveredServiceNotification;
import org.osgi.service.discovery.ServiceEndpointDescription;

public class DiscoveredServiceNotificationImpl implements DiscoveredServiceNotification {

    private ServiceEndpointDescription discription;
    private Collection<String> interfaces;
    private Collection<String> filters;
    private int type;

    public DiscoveredServiceNotificationImpl(ServiceEndpointDescription sd, boolean isFilter, String match, int type) {
        this.discription = sd;
        if (isFilter) {
            filters = new ArrayList<String>();
            filters.add(match);
            interfaces = Collections.emptySet();
        } else {
            interfaces = new HashSet<String>();
            interfaces.add(match);
            filters = Collections.emptyList();
        }

        this.type = type;
    }

    public ServiceEndpointDescription getServiceEndpointDescription() {
        return discription;
    }

    public int getType() {
        return type;
    }

    public Collection<String> getInterfaces() {
        return interfaces;
    }

    public Collection<String> getFilters() {
        return filters;
    }
}
