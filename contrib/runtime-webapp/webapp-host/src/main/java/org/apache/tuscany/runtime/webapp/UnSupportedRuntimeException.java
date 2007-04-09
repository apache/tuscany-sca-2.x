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
 * Thrown when an error is encountered booting the runtme in a web app environment
 *
 * @version $Rev: 439728 $ $Date: 2006-09-03 02:02:44 -0400 (Sun, 03 Sep 2006) $
 */
public class UnSupportedRuntimeException extends TuscanyRuntimeException {

    public UnSupportedRuntimeException() {
    }

    public UnSupportedRuntimeException(String message) {
        super(message);
    }

    public UnSupportedRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnSupportedRuntimeException(Throwable cause) {
        super(cause);
    }
}
