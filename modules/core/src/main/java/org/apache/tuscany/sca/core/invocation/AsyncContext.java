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

package org.apache.tuscany.sca.core.invocation;

/**
 * Allows extensions to associate data with a client's async request.
 */
public interface AsyncContext {
	
    /**
     * Looks up an attribute value by name.
     * @param name The name of the attribute
     * @return The value of the attribute
     */
    public Object getAttribute(String name);

    /**
     * Sets the value of an attribute.
     * 
     * @param name The name of the attribute 
     * @param value
     */
    public void setAttribute(String name, Object value);

}
