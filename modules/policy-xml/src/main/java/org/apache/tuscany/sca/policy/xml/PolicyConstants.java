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
package org.apache.tuscany.sca.policy.xml;

import javax.xml.namespace.QName;

/**
 * constants related to policy framework
 *
 */
public interface PolicyConstants {
    String SCA10_NS = "http://www.osoa.org/xmlns/sca/1.0";
    String INTENT = "intent";
    String INTENT_MAP = "intentMap";
    String NAME = "name";
    String TARGET_NAMESPACE = "targetNamespace";
    String SCA_DEFINITIONS = "definitions";
    String CONSTRAINS = "constrains";
    String DESCRIPTION = "description";
    String PROVIDES = "provides";
    String APPLIES_TO = "appliesTo";
    String QUALIFIER = ".";
    String REQUIRES = "requires";
    
    
    QName POLICY_INTENT_QNAME = new QName(SCA10_NS, INTENT);
    QName POLICY_SET_QNAME = new QName(SCA10_NS, "policySet");
    QName POLICY_INTENT_MAP_QNAME = new QName(SCA10_NS, INTENT_MAP); 
    QName SCA_DEFNS_QNAME = new QName(SCA10_NS, SCA_DEFINITIONS);
    QName DESCRIPTION_QNAME = new QName(SCA10_NS, DESCRIPTION);
    QName POLICY_INTENT_MAP_QUALIFIER_QNAME = new QName(SCA10_NS, QUALIFIER);
    
    String QUALIFIED_INTENT_CONSTRAINS_ERROR = " - Qualified Intents must not specify 'constrains' attribute";
    
    
    
    
    
}
