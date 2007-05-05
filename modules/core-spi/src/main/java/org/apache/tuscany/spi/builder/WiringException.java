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
package org.apache.tuscany.spi.builder;

import java.net.URI;


/**
 * Denotes a general error raised during wiring
 *
 * @version $Rev$ $Date$
 */
public abstract class WiringException extends Exception {
    private final URI sourceUri;
    private final URI targetUri;

    protected WiringException(String message) {
        super(message);
        sourceUri = null;
        targetUri = null;
    }

    protected WiringException(String message, URI sourceUri, URI targetUri) {
        super(message);
        this.sourceUri = sourceUri;
        this.targetUri = targetUri;
    }

    protected WiringException(String message, URI sourceUri, URI targetUri, Throwable cause) {
        super(message, cause);
        this.sourceUri = sourceUri;
        this.targetUri = targetUri;
    }

    /**
     * Returns the source name for the wire
     *
     * @return the source name the source name for the wire
     */
    public URI getSourceUri() {
        return sourceUri;
    }

    /**
     * Returns the target name for the wire
     *
     * @return the target name the source name for the wire
     */
    public URI getTargetUri() {
        return targetUri;
    }

}
