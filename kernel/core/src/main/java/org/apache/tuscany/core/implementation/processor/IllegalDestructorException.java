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
package org.apache.tuscany.core.implementation.processor;

import org.apache.tuscany.spi.implementation.java.ProcessingException;

/**
 * Denotes an illegal signature for a method decorated with {@link org.osoa.sca.annotations.Destroy}
 *
 * @version $Rev$ $Date$
 */
public class IllegalDestructorException extends ProcessingException {
    private static final long serialVersionUID = 365719353107446326L;

    public IllegalDestructorException(String message) {
        super(message);
    }

    public IllegalDestructorException(String message, String identifier) {
        super(message, identifier);
    }
}
