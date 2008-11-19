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

/**
 * Constants for the main supported contribution package types.
 * 
 * @version $Rev$ $Date$
 */
public interface PackageType {
    
    /**
     * Java compressed contribution package
     */
    String JAR = "application/x-compressed";

    /**
     * Zip archive contribution package
     */
    String ZIP = "application/x-compressed";

    /**
     * Filesystem folder contribution package
     */
    String FOLDER = "application/vnd.tuscany.folder";
    
    
    String BUNDLE = "application/osgi.bundle";
    
    /**
     * Java EE Web Application Archive
     */
    String WAR = "application/war";
    
    /**
     * Java EE Enterprise Application Archive
     */
    String EAR = "application/ear";

}
