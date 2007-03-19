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
package org.apache.tuscany.core.component.instancefactory;

import org.apache.tuscany.spi.builder.BuilderException;

/**
 * The builder exception for IF provider.
 * 
 * @version $Revision$ $Date$
 *
 */
@SuppressWarnings("serial")
public class IFProviderBuilderException extends BuilderException {
    
    /**
     * Initializes the message.
     * @param message Initializes the message.
     */
    public IFProviderBuilderException(String message) {
        super(message);
    }
    
    /**
     * Initializes the causer.
     * @param cause Initializes the cause.
     */
    public IFProviderBuilderException(Throwable cause) {
        super(cause);
    }

}
