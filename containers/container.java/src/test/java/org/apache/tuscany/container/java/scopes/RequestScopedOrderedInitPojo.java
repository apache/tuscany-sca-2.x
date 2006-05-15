/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.container.java.scopes;

import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Destroy;

@Scope("REQUEST")
public class RequestScopedOrderedInitPojo {

    private static Object lock = new Object();
    private static int numberInstantied;
    private int initOrder;

    @Init
    public void init() {
        synchronized (RequestScopedOrderedInitPojo.lock) {
            ++RequestScopedOrderedInitPojo.numberInstantied;
            initOrder = RequestScopedOrderedInitPojo.numberInstantied;
        }
    }

    @Destroy
    public void destroy() throws OrderException {
        synchronized (RequestScopedOrderedInitPojo.lock) {
            if (initOrder != RequestScopedOrderedInitPojo.numberInstantied) {
                throw new OrderException("Instance shutdown done out of order");
            }
            --RequestScopedOrderedInitPojo.numberInstantied;
        }
    }

    public int getNumberInstantiated() {
        return RequestScopedOrderedInitPojo.numberInstantied;
    }

    public int getInitOrder() {
        return initOrder;
    }

}
