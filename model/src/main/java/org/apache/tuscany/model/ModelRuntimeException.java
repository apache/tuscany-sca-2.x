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
package org.apache.tuscany.model;

import org.apache.tuscany.common.TuscanyRuntimeException;

/**
 * The root runtime exception for the model.
 * 
 * @version $Rev$ $Date$
 */
public abstract class ModelRuntimeException extends TuscanyRuntimeException {

    public ModelRuntimeException() {
        super();
    }

    public ModelRuntimeException(String message) {
        super(message);
    }

    public ModelRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModelRuntimeException(Throwable cause) {
        super(cause);
    }

}
