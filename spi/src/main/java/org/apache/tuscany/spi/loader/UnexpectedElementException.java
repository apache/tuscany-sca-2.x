/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
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
package org.apache.tuscany.spi.loader;

import javax.xml.namespace.QName;

/**
 * Exception that indicates that an element was found during loading
 * that when loaded resulted in an unexpected type.
 * This should not occur if the document being parsed conforms to its schema.
 * The messages set to the name of the element
 *
 * @version $Rev$ $Date$
 */
public class UnexpectedElementException extends LoaderException {
    public UnexpectedElementException(QName element) {
        super(element.toString());
    }
}
