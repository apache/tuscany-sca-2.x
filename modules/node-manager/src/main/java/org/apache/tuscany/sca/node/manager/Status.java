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

package org.apache.tuscany.sca.node.manager;

public class Status {
    public static String OK ="ok";
    public static String WARNING = "warning";
    public static String CRITICAL = "critical";
    public static String UNAVAILABLE = "unavailable";
    
    private String name;
    private String uri;
    private String status;
    private String statusMessage;
    private long   execution;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public long getExecution() {
        return execution;
    }

    public void setExecution(long execution) {
        this.execution = execution;
    }

    @Override
    public String toString() {
        return "Status [name=" + name
            + ", uri="
            + uri
            + ", status="
            + status
            + ", statusMessage="
            + statusMessage
            + ", execution="
            + execution
            + "]";
    }
}
