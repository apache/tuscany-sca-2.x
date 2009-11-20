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
package org.apache.tuscany.sca.policy;

import javax.xml.namespace.QName;

/**
 * Interface that will abstract various types of policy specifications
 * and attachments for example WS-Policy
 *
 * @version $Rev$ $Date$
 */
public interface PolicyExpression {
    /**
     * Get the QName of the policy expression
     * @return the QName of the policy expression
     */
    QName getName();

    /**
     * Set the QName of the policy expression
     * @param name
     */
    void setName(QName name);

    /**
     * Get the policy definition in the type of the specific domain
     * @return
     */
    <T> T getPolicy();

    /**
     * Set the policy definition
     * @param expression
     */
    <T> void setPolicy(T policy);

    /**
     * 
     * @param unresolved
     */
    void setUnresolved(boolean unresolved);

    /**
     * 
     * @return
     */
    boolean isUnresolved();
}
