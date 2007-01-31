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
package org.apache.tuscany.spi.storer;

import org.apache.tuscany.api.TuscanyException;

/**
 * Base class for all storer exceptions.
 *
 * @version $Rev$ $Date$
 */
@SuppressWarnings("serial")
public class StorerException extends TuscanyException {

    /**
     * Initializes the error message.
     * @param message Error message.
     */
    public StorerException(String message) {
        super(message);
    }

    /**
     * Initializes the root cause.
     * @param cause Root cause for the exception.
     */
    public StorerException(Throwable cause) {
        super(cause);
    }
    
}
