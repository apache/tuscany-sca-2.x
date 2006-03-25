/**
 *
 * Copyright 2006 The Apache Software Foundation
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
package org.apache.tuscany.core.loader;

import org.apache.tuscany.core.config.ConfigurationLoadException;

/**
 * Exception raised if there is a problem configuring a PropertyFactory.
 *
 * @version $Rev$ $Date$
 */
public class InvalidPropertyFactoryException extends ConfigurationLoadException {
    private static final long serialVersionUID = 5017976138519117474L;

    /**
     * Constructor indicating the cause why the property factory could not be created.
     *
     * @param className the name of the class that is intended to be the PropertyFactory
     * @param cause the Throwable that prevented the PropertyFactory from being created
     */
    public InvalidPropertyFactoryException(String className, Throwable cause) {
        super(className);
        initCause(cause);
    }

    /**
     * Returns the name of the property factory implementation class.
     * @return the name of the property factory implementation class
     */
    public String getClassName() {
        return getMessage();
    }
}
