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
package org.apache.tuscany.spi.context;

import org.apache.tuscany.spi.CoreRuntimeException;

/**
 * Denotes an error while performing an operation on a target component implementation instance or proxy
 *
 * @version $Rev: 368822 $ $Date: 2006-01-13 10:54:38 -0800 (Fri, 13 Jan 2006) $
 */
public class TargetException extends CoreRuntimeException {

    public TargetException() {
        super();
    }

    public TargetException(String message) {
        super(message);
    }

    public TargetException(String message, Throwable cause) {
        super(message, cause);
    }

    public TargetException(Throwable cause) {
        super(cause);
    }

}
