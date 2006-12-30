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
package org.apache.tuscany.spi.loader;

/**
 * Exception that indicates an component type was encountered that could not be handled.
 *
 * @version $Rev$ $Date$
 */
public class UnrecognizedComponentTypeException extends LoaderException {
    private final Class<?> type;

    /**
     * Constructor that indicates which component type loader could not be found.
     *
     * @param type the component type type that could not be handled
     */
    public UnrecognizedComponentTypeException(Class<?> type) {
        super("Unrecognized element", type.getName());
        this.type = type;
    }

    public Class<?> getType() {
        return type;
    }
}
