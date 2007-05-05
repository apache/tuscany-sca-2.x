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
package org.apache.tuscany.implementation.java.invocation;


/**
 * Raised when a component has conversational scope but no conversational contract
 *
 * @version $Rev: 487877 $ $Date: 2006-12-16 15:32:16 -0500 (Sat, 16 Dec 2006) $
 */
public class NoConversationalContractException extends Exception {

    private static final long serialVersionUID = -1157790036638157539L;

    public NoConversationalContractException(String message) {
        super(message);
    }
}
