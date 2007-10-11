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
package org.apache.tuscany.sca.policy.logging.jdk;

import java.util.logging.Level;

/**
 * Implementation for policies that could be injected as parameter
 * into the axis2config.
 */
public class JDKLoggingPolicy {
    private String loggerName;
    private String resourceBundleName;
    private Level logLevel;
    private boolean useParentHandlers = false;
    
    public String getLoggerName() {
        return loggerName;
    }
    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }
    public Level getLogLevel() {
        return logLevel;
    }
    public void setLogLevel(Level logLevel) {
        this.logLevel = logLevel;
    }
    public String getResourceBundleName() {
        return resourceBundleName;
    }
    public void setResourceBundleName(String resourceBundleName) {
        this.resourceBundleName = resourceBundleName;
    }
    public boolean isUseParentHandlers() {
        return useParentHandlers;
    }
    public void setUseParentHandlers(boolean useParentHandlers) {
        this.useParentHandlers = useParentHandlers;
    }
}
