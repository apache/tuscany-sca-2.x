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
package org.apache.tuscany.sca.contribution.service;

/**
 * Denotes an exception while reading artifacts inside an SCA contribution.
 *
 * @version $Rev$ $Date$
 */
public class ContributionReadException extends ContributionException {
    public static final int UNDEFINED = -1;
    private static final long serialVersionUID = -7459051598906813461L;
    private String resourceURI;
    private int line = UNDEFINED;
    private int column = UNDEFINED;

    public ContributionReadException(String message) {
        super(message);
    }

    public ContributionReadException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContributionReadException(Throwable cause) {
        super(cause);
    }
    
    /**
     * Returns the location of the resource that was being read.
     *
     * @return the location of the resource that was being read
     */
    public String getResourceURI() {
        return resourceURI;
    }

    /**
     * Sets the location of the resource that was being read.
     *
     * @param resourceURI the location of the resource that was being read
     */
    public void setResourceURI(String resourceURI) {
        this.resourceURI = resourceURI;
    }

    /**
     * Returns the line inside the resource that was being read.
     * @return the line inside the resource that was being read
     */
    public int getLine() {
        return line;
    }

    /**
     * Sets the line inside the resource that was being read.
     * @param line the line inside the resource that was being read
     */
    public void setLine(int line) {
        this.line = line;
    }

    /**
     * Returns the column inside the resource that was being read.
     * @return the column inside the resource that was being read
     */
    public int getColumn() {
        return column;
    }

    /**
     * Sets the column inside the resource that was being read.
     * @param column the column inside the resource that was being read
     */
    public void setColumn(int column) {
        this.column = column;
    }
}
