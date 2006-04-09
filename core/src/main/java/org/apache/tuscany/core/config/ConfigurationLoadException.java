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

/**
 * Exception indicating that there was a problem loading a configuration resource.
 *
 * @version $Rev$ $Date$
 */
public class ConfigurationLoadException extends ConfigurationException {
    private static final long serialVersionUID = -2310798146091959144L;

    private String resourceURI;

    public ConfigurationLoadException() {
    }

    public ConfigurationLoadException(String message) {
        super(message);
    }

    public ConfigurationLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationLoadException(Throwable cause) {
        super(cause);
    }

    /**
     * Returns the location of the resource that was being loaded.
     * @return the location of the resource that was being loaded
     */
    public String getResourceURI() {
        return resourceURI;
    }

    /**
     * Sets the location of the resource that was being loaded.
     * @param resourceURI the location of the resource that was being loaded
     */
    public void setResourceURI(String resourceURI) {
        this.resourceURI = resourceURI;
    }
}
