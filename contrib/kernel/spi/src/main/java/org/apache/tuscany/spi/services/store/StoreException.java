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
package org.apache.tuscany.spi.services.store;

import org.apache.tuscany.api.TuscanyException;

/**
 * Represents a generic exception thrown by a <code>Store</code>
 *
 * @version $Rev$ $Date$
 */
public class StoreException extends TuscanyException {
    private final String owner;

    public StoreException(String message, String owner, String identifier) {
        super(message, identifier);
        this.owner = owner;
    }

    public StoreException(String message, String owner, String identifier, Throwable cause) {
        super(message, identifier, cause);
        this.owner = owner;
    }

    public StoreException(String message, String owner, Throwable cause) {
        super(message, cause);
        this.owner = owner;
    }

    public StoreException(Throwable cause) {
        super(cause);
        owner = null;
    }

    public String getOwner() {
        return owner;
    }
}
