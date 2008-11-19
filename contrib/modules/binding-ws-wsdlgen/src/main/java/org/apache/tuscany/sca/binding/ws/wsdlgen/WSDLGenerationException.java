/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.tuscany.sca.binding.ws.wsdlgen;

import org.apache.tuscany.sca.monitor.Problem;
import org.osoa.sca.ServiceRuntimeException;

public class WSDLGenerationException extends ServiceRuntimeException {
    private static final long serialVersionUID = 1L;
    private Problem problem;

    public WSDLGenerationException() {
        super();
    }

    public WSDLGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public WSDLGenerationException(String message) {
        super(message);
    }

    public WSDLGenerationException(Throwable cause) {
        super(cause);
    }

    public WSDLGenerationException(String message, Throwable cause, Problem problem) {
        super(message, cause);
        this.problem = problem;
    }

    public Problem getProblem() {
        return problem;
    }

}
