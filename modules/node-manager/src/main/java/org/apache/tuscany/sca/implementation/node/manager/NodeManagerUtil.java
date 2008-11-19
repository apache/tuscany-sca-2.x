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

package org.apache.tuscany.sca.implementation.node.manager;

/**
 * Utility methods for node implementation launchers.
 *
 * @version $Rev$ $Date$
 */
public class NodeManagerUtil {

    private static final String TUSCANY_DOMAIN = "TUSCANY_DOMAIN";
    private static final String DEFAULT_DOMAIN = "http://localhost:9990";

    /**
     * Determine the URI of a node configuration. The domain URI can be configured
     * using a TUSCANY_DOMAIN system property or environment variable.
     * 
     * @param nodeName
     * @return
     */
    public static String nodeConfigurationURI(String nodeName) {
        String domain = System.getProperty(TUSCANY_DOMAIN);
        if (domain == null || domain.length() == 0) {
            domain = System.getenv(TUSCANY_DOMAIN);
        }
        if (domain == null || domain.length() ==0) {
            domain = DEFAULT_DOMAIN;
        }
        String nodeConfiguration = domain + "/node-config/" + nodeName;
        return nodeConfiguration;
    }
    
}
