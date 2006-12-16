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

/**
 * Denotes a general error raised during wiring
 *
 * @version $Rev$ $Date$
 */
public abstract class WiringException extends BuilderException {
    private String sourceName;
    private String referenceName;
    private String targetName;
    private String targetServiceName;

    protected WiringException(String message) {
        super(message);
    }

    protected WiringException(String message, String identifier) {
        super(message, identifier);
    }

    protected WiringException(String message, Throwable cause) {
        super(message, cause);
    }

    protected WiringException(Throwable cause) {
        super(cause);
    }

    /**
     * Returns the source name for the wire
     *
     * @return the source name the source name for the wire
     */
    public String getSourceName() {
        return sourceName;
    }

    /**
     * Sets the source name for the wire
     *
     * @param sourceName the source name for the wire
     */
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    /**
     * Returns the target name for the wire
     *
     * @return the target name the source name for the wire
     */
    public String getTargetName() {
        return targetName;
    }

    /**
     * Sets the target name for the wire
     *
     * @param targetName the source name for the wire
     */
    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }


    /**
     * Returns the source reference name for the wire
     *
     * @return the source reference name for the wire
     */
    public String getReferenceName() {
        return referenceName;
    }

    /**
     * Sets the source reference name for the wire
     *
     * @param referenceName the source reference name for the wire
     */
    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    /**
     * Returns the target service name for the wire
     *
     * @return the target service name for the wire
     */
    public String getTargetServiceName() {
        return targetServiceName;
    }

    /**
     * Sets the target service name for the wire
     *
     * @param targetServiceName the target service name for the wire
     */
    public void setTargetServiceName(String targetServiceName) {
        this.targetServiceName = targetServiceName;
    }
}
