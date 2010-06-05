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

package org.apache.tuscany.sca.runtime;

import java.util.ResourceBundle;

public class Version {
    
    public static final String VERSION;
    public static final String REVISION;
    public static final String BUILDTIME;
    static {
        ResourceBundle rb = ResourceBundle.getBundle("org/apache/tuscany/sca/runtime/revision");
        VERSION = rb.getString("version");
        REVISION = rb.getString("revision");
        BUILDTIME = rb.getString("buildtime");
    }

    public static String getVersion() {
        return VERSION;
    }
    
    public static String getRevsion() {
        return REVISION;
    }
    
    public static String getBuildTime() {
        return BUILDTIME;
    }
}
