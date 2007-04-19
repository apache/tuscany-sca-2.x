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
package org.apache.tuscany.core.wire;

import org.apache.tuscany.spi.wire.ProxyCreationException;

/**
 * Thrown when an {@link org.apache.tuscany.spi.model.Operation} cannot be mapped to a method on an interface 
 * @version $Rev$ $Date$
 */
public class NoMethodForOperationException extends ProxyCreationException {
    private static final long serialVersionUID = 5116536602309483679L;

    public NoMethodForOperationException() {
    }

    public NoMethodForOperationException(String message) {
        super(message);
    }

    public NoMethodForOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoMethodForOperationException(Throwable cause) {
        super(cause);
    }
}
