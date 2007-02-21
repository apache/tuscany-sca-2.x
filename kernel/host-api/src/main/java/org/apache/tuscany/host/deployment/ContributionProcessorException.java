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
package org.apache.tuscany.host.deployment;

/**
 * Exception thrown to indicate that a Content-Type is not supported by this SCA Domain.
 * The Content-Type value supplied will be returned as the message text for this exception.
 *
 * @version $Rev: 490357 $ $Date: 2006-12-26 11:06:27 -0800 (Tue, 26 Dec 2006) $
 */
public class ContributionProcessorException extends DeploymentException {

    /**
     * 
     */
    private static final long serialVersionUID = -5187793020502900879L;

    /**
     * @param message the exception message
     */
    public ContributionProcessorException(String message) {
        super(message);
    }

    /**
     * 
     * @param message     the exception message
     * @param identifier  an identifier for this exception
     */
    public ContributionProcessorException(String message, String identifier) {
        super(message, identifier);
    }
    
    /**
     * @param message the exception message
     * @param cause   a cause for the exception
     */
    public ContributionProcessorException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * @param message
     * @param identifier
     * @param cause
     */
    public ContributionProcessorException(String message, String identifier, Throwable cause) {
        super(message, identifier, cause);
    }

    /**
     * @param cause
     */
    public ContributionProcessorException(Throwable cause) {
        super(cause);
    }

    

}
