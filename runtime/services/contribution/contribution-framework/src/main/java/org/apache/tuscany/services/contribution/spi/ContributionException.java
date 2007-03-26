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
package org.apache.tuscany.services.contribution.spi;


/**
 * Base class for exceptions raised by contribution services.
 *
 * @version $Rev: 519710 $ $Date: 2007-03-18 15:19:16 -0700 (Sun, 18 Mar 2007) $
 */
public class ContributionException extends AbstractContributionException {
    /**
     * 
     */
    private static final long serialVersionUID = 4432880414927652578L;

    protected ContributionException() {
        super();
    }

    protected ContributionException(String message) {
        super(message);
    }

    protected ContributionException(String message, String identifier) {
        super(message, identifier);
    }

    protected ContributionException(String message, Throwable cause) {
        super(message, cause);
    }

    protected ContributionException(String message, String identifier, Throwable cause) {
        super(message, identifier, cause);
    }

    public ContributionException(Throwable cause) {
        super(cause);
    }
}
