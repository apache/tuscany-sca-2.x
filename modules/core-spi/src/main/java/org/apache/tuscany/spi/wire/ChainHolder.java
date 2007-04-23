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
package org.apache.tuscany.spi.wire;

/**
 * A holder used to associate an wire chain with a local copy of a target invoker that was previously cloned from the
 * chain master
 *
 * @version $Rev$ $Date$
 */
public class ChainHolder implements Cloneable {
    InvocationChain chain;
    TargetInvoker cachedInvoker;

    public ChainHolder(InvocationChain config) {
        this.chain = config;
    }

    public InvocationChain getChain() {
        return chain;
    }

    public TargetInvoker getCachedInvoker() {
        return cachedInvoker;
    }

    public void setCachedInvoker(TargetInvoker invoker) {
        this.cachedInvoker = invoker;
    }

    @Override
    public ChainHolder clone() {
        try {
            return (ChainHolder) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
