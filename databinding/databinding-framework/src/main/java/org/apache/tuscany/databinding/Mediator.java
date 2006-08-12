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
package org.apache.tuscany.databinding;

/**
 * This interface will be used as a Tuscany system service to perform data mediations
 * 
 * Mediate the data from one binding type to the other one
 *
 */
public interface Mediator {
    /**
     * @param source The source object to be mediated
     * @param sourceType The type of the source data
     * @param resultType The type of the result data
     * @param context The context
     * @return The mediated result
     */
    public Object mediate(Object source, Object sourceType, Object resultType, TransformationContext context);
    /**
     * @param source
     * @param target
     * @param sourceType
     * @param resultType
     * @param context
     * @return
     */
    public Object mediate(Object source, Object target, Object sourceType, Object resultType, TransformationContext context);
    
}
