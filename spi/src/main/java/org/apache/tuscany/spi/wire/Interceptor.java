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
package org.apache.tuscany.spi.wire;

/**
 * Synchronous, around-style mediation associated with a client- or target- side wire.
 *
 * @version $Rev: 394379 $ $Date: 2006-04-15 15:01:36 -0700 (Sat, 15 Apr 2006) $
 */
public interface Interceptor {

    /**
     * Process a synchronous wire
     *
     * @param msg the request Message for the wire
     * @return the response Message from the wire
     */
    Message invoke(Message msg);

    /**
     * Sets the next interceptor
     */
    void setNext(Interceptor next);

    /**
     * Returns the next interceptor or null
     */
    Interceptor getNext();

    /**
     * Returns true if the interceptor can be optimized away from a wire
     */
    boolean isOptimizable();
}
