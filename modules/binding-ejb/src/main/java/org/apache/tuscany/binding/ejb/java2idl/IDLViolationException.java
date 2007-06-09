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
package org.apache.tuscany.binding.ejb.java2idl;

import org.osoa.sca.ServiceRuntimeException;

/**
 * Exception denoting an RMI/IIOP Java-To-IDL mapping spec violation.
 */
public class IDLViolationException extends ServiceRuntimeException {

    private static final long serialVersionUID = 3334304081465286631L;

    /**
     * The section violated.
     */
    private String section;

    public IDLViolationException(String msg) {
        super(msg);
    }

    public IDLViolationException(String msg, String section) {
        this(msg);
        this.section = section;
    }

    /**
     * Return the section violated.
     */
    public String getSection() {
        return section;
    }
}
