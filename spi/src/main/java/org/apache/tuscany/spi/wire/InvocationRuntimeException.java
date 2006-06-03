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

import org.osoa.sca.ServiceRuntimeException;

/**
 * Denotes a runtime exception thrown during an invocation over a wire
 *
 * @version $Rev: 396284 $ $Date: 2006-04-23 16:27:42 +0100 (Sun, 23 Apr 2006) $
 */
public class InvocationRuntimeException extends ServiceRuntimeException {

    public InvocationRuntimeException() {
        super();
    }

    public InvocationRuntimeException(String message) {
        super(message);
    }

    public InvocationRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvocationRuntimeException(Throwable cause) {
        super(cause);
    }

}
