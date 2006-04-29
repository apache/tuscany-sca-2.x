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
package org.apache.tuscany.core.config;

import org.apache.tuscany.core.assembly.JavaExtensibilityElement;
import org.apache.tuscany.core.assembly.impl.JavaExtensibilityElementImpl;
import org.apache.tuscany.model.assembly.Extensible;

/**
 * Provides utility methods for {@link JavaExtensibilityElement} such as looking it up in a {@link
 * org.apache.tuscany.model.assembly.ComponentInfo} extensibility collection
 *
 * @version $$Rev$$ $$Date$$
 */
public class JavaExtensibilityHelper {

    private JavaExtensibilityHelper() {
    }

    /**
     * Returns the Java extensibility element stored in the extensible model artifacts extensibility
     * collection or creates a new one if not found
     */
    @SuppressWarnings("unchecked")
    public static JavaExtensibilityElement getExtensibilityElement(Extensible extensible) {
        for (Object o : extensible.getExtensibilityElements()) {
            if (o instanceof JavaExtensibilityElement) {
                return (JavaExtensibilityElement) o;
            }
        }
        JavaExtensibilityElement element = new JavaExtensibilityElementImpl();
        extensible.getExtensibilityElements().add(element);
        return element;
    }
}
