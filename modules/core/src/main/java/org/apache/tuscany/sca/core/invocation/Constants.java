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
 * Constants used during invocation in the runtime
 *
 */
public interface Constants {
    public static final String MESSAGE_ID 				= "MESSAGE_ID";
    public static final String RELATES_TO 				= "RELATES_TO";
    public static final String ASYNC_RESPONSE_INVOKER 	= "ASYNC_RESPONSE_INVOKER";
    public static final String ASYNC_CALLBACK 			= "ASYNC_CALLBACK";
    public static final String CALLBACK                 = "CALLBACK";
    
    /**
     *  If you've set the TCCL in your binding impl according to OASIS rules you can prevent
     *  the implementation provider from repeating the process by including this header
     */
    public static final String SUPPRESS_TCCL_SWAP       = "SUPPRESS_TCCL_SWAP";
}
