/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.component;

import org.apache.tuscany.spi.context.TargetException;

/**
 * Denotes an exception while resolving an automatic wire
 * 
 * @version $Rev$ $Date$
 */
public class AutowireResolutionException extends TargetException {

    public AutowireResolutionException() {
        super();
    }

    public AutowireResolutionException(String message) {
        super(message);
    }

    public AutowireResolutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public AutowireResolutionException(Throwable cause) {
        super(cause);
    }

}

