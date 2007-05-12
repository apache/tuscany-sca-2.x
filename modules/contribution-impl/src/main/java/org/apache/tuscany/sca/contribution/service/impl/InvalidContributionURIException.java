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
package org.apache.tuscany.sca.contribution.service.impl;

import org.apache.tuscany.contribution.service.ContributionException;


/**
 * Exception that indicates that the supplied contribution URI is invalid or inexistent.
 *
 * @version $Rev: 511466 $ $Date: 2007-02-25 00:45:22 -0800 (Sun, 25 Feb 2007) $
 */
public class InvalidContributionURIException extends ContributionException {

    /**
     * 
     */
    private static final long serialVersionUID = -3184477070625689942L;

    protected InvalidContributionURIException() {
    }

    protected InvalidContributionURIException(String message) {
        super(message);
    }

    protected InvalidContributionURIException(String message, String identifier) {
        super(message, identifier);
    }

    protected InvalidContributionURIException(String message, Throwable cause) {
        super(message, cause);
    }

    protected InvalidContributionURIException(String message, String identifier, Throwable cause) {
        super(message, identifier, cause);
    }

    protected InvalidContributionURIException(Throwable cause) {
        super(cause);
    }
}
