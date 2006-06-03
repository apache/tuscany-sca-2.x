/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.apache.tuscany.spi.component;

import org.apache.tuscany.spi.CoreRuntimeException;

/**
 * An unchecked exception encountered by an {@link SCAObject}
 *
 * @version $Rev$ $Date$
 */
public class ComponentRuntimeException extends CoreRuntimeException {

    public ComponentRuntimeException() {
        super();
    }

    public ComponentRuntimeException(String message) {
        super(message);
    }

    public ComponentRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ComponentRuntimeException(Throwable cause) {
        super(cause);
    }

}
