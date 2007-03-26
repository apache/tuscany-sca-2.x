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
 * Exception thrown to indicate that a Content-Type is not supported by this SCA Domain.
 * The Content-Type value supplied will be returned as the message text for this exception.
 *
 * @version $Rev$ $Date$
 */
public class UnsupportedContentTypeException extends ContributionException {
    private static final long serialVersionUID = -1831797280021355672L;

    /**
     * Constructor specifying the Content-Type value that is not supported.
     *
     * @param contentType the type that is not supported
     */
    public UnsupportedContentTypeException(String contentType) {
        super(contentType);
    }

    /**
     * Constructor specifying the Content-Type value that is not supported
     * and an identifier to use with this exception (typically the resource being processed).
     *
     * @param contentType the type that is not supported
     * @param identifier  an identifier for this exception
     */
    public UnsupportedContentTypeException(String contentType, String identifier) {
        super(contentType, identifier);
    }
}
