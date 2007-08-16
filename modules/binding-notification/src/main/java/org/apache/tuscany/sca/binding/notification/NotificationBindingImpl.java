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
package org.apache.tuscany.sca.binding.notification;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.impl.BaseImpl;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySet;

/**
 * @version $Rev$ $Date$
 */
public class NotificationBindingImpl extends BaseImpl implements NotificationBinding {
    private String name;
    private String uri;
    protected String ntmAddress;
    protected URI notificationType;
    
    public Object clone() {
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getURI() {
        return uri;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }

    public String getNtmAddress() {
        return ntmAddress;
    }
    
    public void setNtmAddress(String ntmAddress) {
        this.ntmAddress = ntmAddress;
    }
    
    public URI getNotificationType() {
        return notificationType;
    }
    
    public void setNotificationType(URI notificationType) {
        this.notificationType = notificationType;
    }
}
