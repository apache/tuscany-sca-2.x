/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tuscany.spi.component;


/**
 * An checked exception encountered by an {@link org.apache.tuscany.spi.component.Component}
 *
 * @version $Rev$ $Date$
 */
public abstract class ComponentException extends Exception {

    public ComponentException() {
        super();
    }

    public ComponentException(String message, Throwable cause) {
        super(message, cause);
    }

    public ComponentException(String message) {
        super(message);
    }

    public ComponentException(Throwable cause) {
        super(cause);
    }

}
