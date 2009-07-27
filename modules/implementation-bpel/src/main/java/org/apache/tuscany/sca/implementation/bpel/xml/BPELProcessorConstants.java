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
package org.apache.tuscany.sca.implementation.bpel.xml;

import javax.xml.namespace.QName;


/**
 * BPEL Constants
 * 
 * @version $Rev$ $Date$
 */
public class BPELProcessorConstants {
    static final String SCA_BPEL_NS 	= "http://docs.oasis-open.org/ns/opencsa/sca-bpel/200801";
    static final String WSDL_NS 		= "http://schemas.xmlsoap.org/wsdl/";

    // BPEL 1.1
    static final String BPEL_NS 		= "http://schemas.xmlsoap.org/ws/2004/03/business-process/";
    static final String BPEL_PLINK_NS 	= "http://schemas.xmlsoap.org/ws/2004/03/partner-link/";
    final static String NAME_ELEMENT 		= "name";
    static final String LINKTYPE_NAME 		= "partnerLinkType";
    final static String TARGET_NAMESPACE 	= "targetNamespace";
    static final QName PROCESS_ELEMENT 		= new QName(BPEL_NS, "process");
    static final QName PARTNERLINK_ELEMENT 	= new QName(BPEL_NS, "partnerLink");
    static final QName ONEVENT_ELEMENT 		= new QName(BPEL_NS, "onEvent");
    static final QName RECEIVE_ELEMENT 		= new QName(BPEL_NS, "receive");
    static final QName ONMESSAGE_ELEMENT 	= new QName(BPEL_NS, "onMessage");
    static final QName INVOKE_ELEMENT 		= new QName(BPEL_NS, "invoke");
    static final QName IMPORT_ELEMENT 		= new QName(BPEL_NS, "import");
    static final QName VARIABLE_ELEMENT		= new QName(BPEL_NS, "variable");
    static final QName LINKTYPE_ELEMENT 	= new QName(BPEL_PLINK_NS, LINKTYPE_NAME);
    
    // BPEL 2.0
    static final String BPEL_NS_20 			= "http://docs.oasis-open.org/wsbpel/2.0/process/executable";
    static final String BPEL_PLINK_NS_20	= "http://docs.oasis-open.org/wsbpel/2.0/plnktype";
    static final QName PROCESS_ELEMENT_20 		= new QName(BPEL_NS_20, "process");
    static final QName PARTNERLINK_ELEMENT_20 	= new QName(BPEL_NS_20, "partnerLink");
    static final QName ONEVENT_ELEMENT_20 		= new QName(BPEL_NS_20, "onEvent");
    static final QName RECEIVE_ELEMENT_20 		= new QName(BPEL_NS_20, "receive");
    static final QName ONMESSAGE_ELEMENT_20 	= new QName(BPEL_NS_20, "onMessage");
    static final QName INVOKE_ELEMENT_20 		= new QName(BPEL_NS_20, "invoke");
    static final QName IMPORT_ELEMENT_20 		= new QName(BPEL_NS_20, "import");   
    static final QName VARIABLE_ELEMENT_20		= new QName(BPEL_NS_20, "variable");
    static final QName LINKTYPE_ELEMENT_20 		= new QName(BPEL_PLINK_NS_20, LINKTYPE_NAME);
}
