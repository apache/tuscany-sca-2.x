/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.     
 */
package org.apache.tuscany.rmi;


/**
 * This exception will relate to situations where the end applicaition's input is the cause of the exception
 *
 * @version $Rev: 486986 $ $Date: 2006-12-14 11:48:28 +0530 (Thu, 14 Dec 2006) $
 */
public class RMIHostException extends RuntimeException {
 
    private static final long serialVersionUID = 3378300080918544410L;

    public RMIHostException() {
    }

    public RMIHostException(String message) {
        super(message);
    }

    public RMIHostException(Throwable e) {
        super(e);
    }

    public RMIHostException(String message, Throwable cause) {
        super(message, cause);
    }
}
