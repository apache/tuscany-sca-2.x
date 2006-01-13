/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.addressing;

/**
 */
public interface AddressingConstants {

    String NS_URI = "http://schemas.xmlsoap.org/ws/2004/08/addressing";
    String TO_HEADER_NAME = NS_URI + "#To";
    String FROM_HEADER_NAME = NS_URI + "#From";
    String MESSAGE_ID_HEADER_NAME = NS_URI + "#MessageID";
    String ACTION_HEADER_NAME = NS_URI + "#Action";
    String REPLY_TO_HEADER_NAME = NS_URI + "#ReplyTo";
    String RELATES_TO_HEADER_NAME = NS_URI + "#RelatesTo";
    String FAULT_TO_HEADER_NAME = NS_URI + "#FaultTo";
    String ENDPOINT_REFERENCE_HEADER_NAME = NS_URI + "#EndpointReference";

}
