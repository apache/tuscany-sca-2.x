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

/**
 * A serialization contract for runtime SCA artifacts. When an instance is deserialized, runtime services defined in
 * this contract must be set before reactivating the instance
 *
 * @version $Rev$ $Date$
 */
public interface SCAExternalizable {

    /**
     * Sets the current work context
     *
     * @param context the current work context
     */
    void setWorkContext(WorkContext context);

    /**
     * Callback after all values have been set prior to making the instance available in the runtime
     *
     * @throws org.apache.tuscany.spi.component.ReactivationException
     *
     */
    void reactivate() throws ReactivationException;
}
