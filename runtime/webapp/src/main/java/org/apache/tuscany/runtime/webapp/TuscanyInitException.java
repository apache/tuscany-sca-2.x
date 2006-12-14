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
package org.apache.tuscany.runtime.webapp;

import org.apache.tuscany.api.TuscanyRuntimeException;

/**
 * Exception indicating that there was a problem starting the Tuscany runtime.
 *
 * @version $Rev$ $Date$
 */
public class TuscanyInitException extends TuscanyRuntimeException {
    public TuscanyInitException(String string) {
        super(string);
    }

    public TuscanyInitException(String message, String identifier) {
        super(message, identifier);
    }

    public TuscanyInitException(String string, Throwable throwable) {
        super(string, throwable);
    }

    public TuscanyInitException(String message, String identifier, Throwable cause) {
        super(message, identifier, cause);
    }

    public TuscanyInitException(Throwable throwable) {
        super(throwable);
    }
}
