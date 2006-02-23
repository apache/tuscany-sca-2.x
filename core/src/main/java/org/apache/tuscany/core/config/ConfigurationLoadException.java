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
package org.apache.tuscany.core.config;

import java.io.IOException;

/**
 * Exception indicating that there was a problem loading a configuration resource.
 *
 * @version $Rev$ $Date$
 */
public class ConfigurationLoadException extends ConfigurationException {

    /**
     * Constructor specifying the resource that was being loaded and the IOException that resulted.
     * These are returned as the message and cause respectively.
     *
     * @param resource the resource being loaded
     * @param cause the IOException that occurred
     */
    public ConfigurationLoadException(String resource, IOException cause) {
        super(resource, cause);
    }

    /**
     * Constructor specifying the resource that was being loaded.
     *
     * @param resource the resource being loaded
     */
    public ConfigurationLoadException(String resource) {
        super(resource);
    }
}
