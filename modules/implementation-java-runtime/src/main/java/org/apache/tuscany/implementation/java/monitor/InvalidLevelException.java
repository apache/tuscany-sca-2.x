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
package org.apache.tuscany.implementation.java.monitor;

/**
 * Exception indicating an invalid log level has been passed.
 *
 * @version $Rev$ $Date$
 */
public class InvalidLevelException extends IllegalArgumentException {
    private static final long serialVersionUID = 7767234706427841915L;
    private final String method;
    private final String level;

    /**
     * Constructor specifying the method name and the level affected.
     *
     * @param method the name of the method being monitored
     * @param level  the invalid log level value
     */
    public InvalidLevelException(String method, String level) {
        super();
        this.method = method;
        this.level = level;
    }

    /**
     * Returns the name of the method being monitored.
     *
     * @return the name of the method being monitored
     */
    public String getMethod() {
        return method;
    }

    /**
     * Returns the invalid log level specified.
     *
     * @return the invalid log level that was specified
     */
    public String getLevel() {
        return level;
    }

    public String getMessage() {
        return "Invalid level for method " + method + " : " + level;
    }
}
