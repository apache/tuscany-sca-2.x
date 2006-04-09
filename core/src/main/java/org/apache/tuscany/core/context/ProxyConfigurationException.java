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
package org.apache.tuscany.core.context;

import org.apache.tuscany.core.config.ConfigurationException;

/**
 *
 * @version $Rev$ $Date$
 */
public class ProxyConfigurationException extends ConfigurationException {
    private static final long serialVersionUID = -5860342620108741058L;

    public ProxyConfigurationException() {
    }

    public ProxyConfigurationException(String message) {
        super(message);
    }

    public ProxyConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProxyConfigurationException(Throwable cause) {
        super(cause);
    }
}
