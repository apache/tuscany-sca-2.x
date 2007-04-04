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
package org.apache.tuscany.services.spi.contribution;


/**
 * Base class for Exceptions raised during the loading process. Loader implementations should throw a subclass of this
 * to indicate the actual problem.
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
     * Returns the location of the resource that was being loaded.
     *
     * @return the location of the resource that was being loaded
     */
    public String getResourceURI() {
        return resourceURI;
    }

    /**
     * Sets the location of the resource that was being loaded.
     *
     * @param resourceURI the location of the resource that was being loaded
     */
    public void setResourceURI(String resourceURI) {
        this.resourceURI = resourceURI;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }
}
