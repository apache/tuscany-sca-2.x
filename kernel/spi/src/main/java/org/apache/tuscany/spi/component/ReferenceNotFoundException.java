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
package org.apache.tuscany.spi.component;

import org.apache.tuscany.spi.CoreRuntimeException;

/**
 * Thrown when a component reference is not defined in the compoenent type
 *
 * @version $$Rev$$ $$Date$$
 */
public class ReferenceNotFoundException extends CoreRuntimeException {
    private static final long serialVersionUID = 737170534780079573L;

    public ReferenceNotFoundException() {
    }

    public ReferenceNotFoundException(String message) {
        super(message);
    }

    public ReferenceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReferenceNotFoundException(Throwable cause) {
        super(cause);
    }
}
