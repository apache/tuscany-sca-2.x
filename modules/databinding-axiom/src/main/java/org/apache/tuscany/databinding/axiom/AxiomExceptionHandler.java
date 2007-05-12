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

package org.apache.tuscany.databinding.axiom;

import org.apache.axiom.om.OMElement;
import org.apache.tuscany.databinding.ExceptionHandler;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.sca.interfacedef.util.FaultException;
import org.apache.tuscany.sca.interfacedef.util.XMLType;

/**
 * AXIOM implementation of ExceptionHandler
 * 
 * @version $Rev$ $Date$
 */
public class AxiomExceptionHandler implements ExceptionHandler {

    public Exception createException(DataType<DataType> exceptionType, String message, Object faultInfo, Throwable cause) {
        return new FaultException(message, (OMElement)faultInfo, cause);
    }

    public Object getFaultInfo(Exception exception) {
        if (exception == null) {
            return null;
        }
        FaultException faultException = (FaultException)exception;
        return faultException.getFaultInfo();

    }

    public DataType<?> getFaultType(DataType exceptionType) {
        if (FaultException.class == exceptionType.getPhysical()) {
            XMLType type = XMLType.UNKNOWN;
            if(exceptionType.getLogical() instanceof XMLType) {
                type = (XMLType) exceptionType.getLogical();
            }
            DataType<XMLType> faultType = new DataTypeImpl<XMLType>(AxiomDataBinding.NAME, OMElement.class, type);
            return faultType;
        } else {
            return null;
        }
    }
}
