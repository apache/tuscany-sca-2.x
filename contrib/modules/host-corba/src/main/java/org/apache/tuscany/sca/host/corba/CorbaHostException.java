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

package org.apache.tuscany.sca.host.corba;

/**
 * @version $Rev$ $Date$
 * General exception for corba hosts operations
 */
public class CorbaHostException extends Exception {

    private static final long serialVersionUID = 1L;

    public static final String BINDING_IN_USE = "Binding name is already in use";
    public static final String NO_SUCH_OBJECT = "There is no object under given location";
    public static final String NO_SUCH_HOST = "Couldn't find specified host";
    public static final String NO_SUCH_PORT = "Couldn't connect to specified port";
    public static final String WRONG_NAME = "Characters used in binding name are illegal";

    public CorbaHostException(String message) {
        super(message);
    }

    public CorbaHostException(Exception cause) {
        super(cause);
    }

    public CorbaHostException(String message, Exception cause) {
        super(message, cause);
    }
}
