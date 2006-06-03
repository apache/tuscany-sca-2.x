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
package org.apache.tuscany.spi.loader;

import javax.xml.namespace.QName;

/**
 * Exception that indicates an element was encountered that could not be handled.
 *
 * @version $Rev: 392764 $ $Date: 2006-04-09 09:13:55 -0700 (Sun, 09 Apr 2006) $
 */
public class UnrecognizedElementException extends LoaderException {
    private static final long serialVersionUID = 2549543622209829032L;
    private final QName element;

    /**
     * Constructor that indicates which resource could not be found. The supplied parameter is also returned
     * as the message.
     *
     * @param element the element that could not be handled
     */
    public UnrecognizedElementException(QName element) {
        super(element.toString());
        setIdentifier(getMessage());
        this.element = element;
    }

    public QName getElement() {
        return element;
    }
}
