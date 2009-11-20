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
package org.apache.tuscany.sca.assembly;

/**
 * TODO RRB experiment. 
 * Represents a binding implemented using Request/Response binding chains
 * Used to test the RRB idea hence no integrated into the Binding interface, yet
 * 
 * @version $Rev$ $Date$
 */
public interface BindingRRB extends Binding {

    WireFormat getRequestWireFormat();

    void setRequestWireFormat(WireFormat wireFormat);

    WireFormat getResponseWireFormat();

    void setResponseWireFormat(WireFormat wireFormat);

    OperationSelector getOperationSelector();

    void setOperationSelector(OperationSelector operationSelector);

}
