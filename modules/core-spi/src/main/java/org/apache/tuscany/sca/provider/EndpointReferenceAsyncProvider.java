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

package org.apache.tuscany.sca.provider;


/**
 * TUSCANY-3783
 * 
 * Async related operations that are here rather than higher up
 * while we develop them. 
 * 
 */
public interface EndpointReferenceAsyncProvider extends EndpointReferenceProvider {

    /**
     * TUSCANY-3801 
     * Returns true if the reference binding provider is natively able
     * to receive async responses. 
     * 
     * @return true if the service provide support async operation natively
     */
    boolean supportsNativeAsync();
}
