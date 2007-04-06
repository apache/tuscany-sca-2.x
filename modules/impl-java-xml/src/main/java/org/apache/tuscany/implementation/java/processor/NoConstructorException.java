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
package org.apache.tuscany.implementation.java.processor;

import org.apache.tuscany.implementation.java.introspection.ProcessingException;

/**
 * Thrown when a suitable constructor for a component implementation cannot be found
 *
 * @version $Rev$ $Date$
 */
public class NoConstructorException extends ProcessingException {
    private static final long serialVersionUID = 3086706387280694424L;
    
    public NoConstructorException() {
    }
    
    public NoConstructorException(String message) {
       super(message);
    }
}
