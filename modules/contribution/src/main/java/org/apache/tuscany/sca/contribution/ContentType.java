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

package org.apache.tuscany.sca.contribution;

public interface ContentType {
    /**
     * Archive specific content types
     */
    String JAR = "application/x-compressed";
    String FOLDER = "application/vnd.tuscany.folder";

    /**
     * Artifact/File specific content types
     */
    String CONTRIBUTION_METADATA = "application/vnd.tuscany.contribution.metadata";
    String COMPOSITE = "application/vnd.tuscany.composite";
    String WSDL = "application/vnd.tuscany.wsdl";
    String JAVA = "application/java-vm";
    
    /**
     * Unkown contentType
     */
    String UNKNOWN = "content/unknown";
 
}
