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

package org.apache.tuscany.spi.idl;

import javax.xml.namespace.QName;

import org.apache.tuscany.api.TuscanyException;

/**
 * The generic java exception to wrap service faults
 * 
 * @version $Rev$ $Date$
 */
public class ServiceFaultException extends TuscanyException {
    private static final long serialVersionUID = -8002583655240625792L;
    private Object faultInfo;
    private QName logical;

    /**
     * @param message
     * @param faultInfo
     */
    public ServiceFaultException(String message, Object faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * @param message
     * @param faultInfo
     * @param cause
     */
    public ServiceFaultException(String message, Object faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * @return the faultInfo
     */
    public Object getFaultInfo() {
        return faultInfo;
    }

    public QName getLogical() {
        return logical;
    }

    public void setLogical(QName logical) {
        this.logical = logical;
    }

    public boolean isMatchingType(Object type) {
        if (logical == null) {
            return false;
        }

        if ((type instanceof QName) && logical.equals(type)) {
            return true;
        }
        if (type instanceof XMLType && logical.equals(((XMLType)type).getElementName())) {
            return true;
        }
        return false;
    }

}
