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

import javax.xml.namespace.QName;

/**
 * Configuration exception that indicates the actual root element in an XML file was not the one expected.
 *
 * @version $Rev$ $Date$
 */
public class InvalidRootElementException extends ConfigurationLoadException {
    private static final long serialVersionUID = 2376629433948140418L;

    private final QName expected;
    private final QName actual;

    public InvalidRootElementException(QName expected, QName actual) {
        super("Invalid root element, expected [" + expected + "], was [" + actual + ']');
        this.expected = expected;
        this.actual = actual;
    }

    public QName getExpected() {
        return expected;
    }

    public QName getActual() {
        return actual;
    }
}
