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
package org.apache.tuscany.sca.databinding.job;

import org.apache.tuscany.sca.databinding.ExceptionHandler;
import org.apache.tuscany.sca.interfacedef.DataType;

public class JobExceptionHandler implements ExceptionHandler {

    public Exception createException(DataType<DataType> exceptionType,
            String message, Object faultInfo, Throwable cause) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object getFaultInfo(Exception exception) {
        // TODO Auto-generated method stub
        return null;
    }

    public DataType<?> getFaultType(DataType exceptionDataType) {
        // TODO Auto-generated method stub
        return null;
    }

}
