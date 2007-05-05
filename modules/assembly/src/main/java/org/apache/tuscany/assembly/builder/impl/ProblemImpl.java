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

package org.apache.tuscany.assembly.builder.impl;

import org.apache.tuscany.assembly.builder.Problem;


/**
 * Reports a composite assembly problem. 
 *
 * @version $Rev$ $Date$
 */
public class ProblemImpl implements Problem {
    
    private String message;
    private Severity severity;
    private Object model;
    private Object resource;
    private Exception cause;

    /**
     * Constructs a new problem.
     * 
     * @param severity
     * @param message
     * @param model
     */
    public ProblemImpl(Severity severity, String message, Object model) {
        this.severity = severity;
        this.message = message;
        this.model = model;
    }

    /**
     * Constructs a new problem.
     * 
     * @param severity
     * @param message
     * @param model
     * @param resource
     */
    public ProblemImpl(Severity severity, String message, Object model, Object resource) {
        this.severity = severity;
        this.message = message;
        this.model = model;
        this.resource = resource;
    }

    /**
     * Constructs a new problem.
     * 
     * @param severity
     * @param message
     * @param cause
     */
    public ProblemImpl(Severity severity, String message, Exception cause) {
        this.severity = severity;
        this.message = message;
        this.cause = cause;
    }

    public Severity getSeverity() {
        return severity;
    }

    public String getMessage() {
        return message;
    }
    
    public Object getModel() {
        return model;
    }

    public Object getResource() {
        return resource;
    }
    
    public Exception getCause() {
        return cause;
    }
}
