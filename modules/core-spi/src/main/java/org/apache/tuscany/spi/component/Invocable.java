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

import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * @version $Rev$ $Date$
 */
public interface Invocable extends SCAObject {

    /**
     * Callback to create a {@link org.apache.tuscany.spi.wire.TargetInvoker}
     * which dispatches to a service offered this artifact
     * 
     * @param targetName the service name
     * @param operation the operation to invoke
     * @param isCallback To indicate if the operation if for callback
     * @return the target invoker
     * @throws TargetInvokerCreationException
     */
    TargetInvoker createTargetInvoker(String targetName, Operation operation, boolean isCallback)
        throws TargetInvokerCreationException;

}
