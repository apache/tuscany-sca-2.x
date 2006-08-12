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
package org.apache.tuscany.spi.policy;

import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.tuscany.spi.wire.InboundWire;

/**
 * Implementations contribute {@link org.apache.tuscany.spi.wire.Interceptor}s or {@link
 * org.apache.tuscany.spi.wire.MessageHandler}s that handle target-side policy on a wire.
 *
 * @version $$Rev$$ $$Date$$
 */
public interface TargetPolicyBuilder {

    void build(ServiceDefinition serviceDefinition, InboundWire<?> wire) throws BuilderException;

}
