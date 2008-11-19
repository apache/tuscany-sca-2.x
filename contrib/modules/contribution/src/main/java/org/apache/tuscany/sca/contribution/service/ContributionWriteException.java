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
 * Denotes an exception while writing artifacts inside an SCA contribution.
 *
 * @version $Rev$ $Date$
 */
public class ContributionWriteException extends ContributionException {
    private static final long serialVersionUID = -7459051598906813461L;
    private String resourceURI;

    public ContributionWriteException(String message) {
        super(message);
    }

    public ContributionWriteException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContributionWriteException(Throwable cause) {
        super(cause);
    }
    
    /**
     * Returns the location of the resource that was being written.
     *
     * @return the location of the resource that was being written
     */
    public String getResourceURI() {
        return resourceURI;
    }

    /**
     * Sets the location of the resource that was being written.
     *
     * @param resourceURI the location of the resource that was being written
     */
    public void setResourceURI(String resourceURI) {
        this.resourceURI = resourceURI;
    }

}
