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
package org.apache.tuscany.test.interop.client;

import java.rmi.RemoteException;

import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;
import org.soapinterop.ComplexDocument;
import org.soapinterop.DocTestPortType;
import org.soapinterop.SimpleDocument1;
import org.soapinterop.SingleTag;

/**
 * This class implements the DocTest service component.
 */
@Service(DocTestPortType.class)
//FIXME workaround for JIRA TUSCANY-41
@Scope("COMPOSITE")
public class LoopbackInteropDocServiceComponentImpl implements DocTestPortType {
    
    public ComplexDocument ComplexDocument(ComplexDocument param0) throws RemoteException {
        return param0;
    }
    
    public SimpleDocument1 SimpleDocument(SimpleDocument1 param4) throws RemoteException {
        return param4;
    }
    
    public SingleTag SingleTag(SingleTag param2) throws RemoteException {
        return param2;
    }
}
