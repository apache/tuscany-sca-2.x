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
package org.apache.tuscany.sca.extensibility.impl;

/**
 * A Framework exception used to indicate that a filter string has an invalid
 * syntax.
 * 
 * <p>
 * An <code>InvalidSyntaxException</code> object indicates that a filter
 * string parameter has an invalid syntax and cannot be parsed. 
 * 
 * <p>
 * This exception conforms to the general purpose exception chaining mechanism.
 * 
 */

public class InvalidSyntaxException extends RuntimeException {
    static final long serialVersionUID = -4295194420816491875L;
    /**
     * The invalid filter string.
     */
    private final String filter;

    /**
     * Creates an exception of type <code>InvalidSyntaxException</code>.
     * 
     * <p>
     * This method creates an <code>InvalidSyntaxException</code> object with
     * the specified message and the filter string which generated the
     * exception.
     * 
     * @param msg The message.
     * @param filter The invalid filter string.
     */
    public InvalidSyntaxException(String msg, String filter) {
        super(msg);
        this.filter = filter;
    }

    /**
     * Creates an exception of type <code>InvalidSyntaxException</code>.
     * 
     * <p>
     * This method creates an <code>InvalidSyntaxException</code> object with
     * the specified message and the filter string which generated the
     * exception.
     * 
     * @param msg The message.
     * @param filter The invalid filter string.
     * @param cause The cause of this exception.
     * @since 1.3
     */
    public InvalidSyntaxException(String msg, String filter, Throwable cause) {
        super(msg, cause);
        this.filter = filter;
    }

    /**
     * Returns the filter string that generated the
     * <code>InvalidSyntaxException</code> object.
     * 
     * @return The invalid filter string.
     * @see BundleContext#getServiceReferences
     * @see BundleContext#addServiceListener(ServiceListener,String)
     */
    public String getFilter() {
        return filter;
    }
}