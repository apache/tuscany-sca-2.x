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

package org.apache.tuscany.sca.policy.util;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.extensibility.ServiceDeclaration;

/**
 * PolicyHanlder tuples stored in policy handler services files
 *
 * @version $Rev$ $Date$
 */
public class PolicyHandlerTuple {
    private ServiceDeclaration declaration;
    private String policyHandlerClassName;
    private QName providedIntentName;
    private String policyModelClassName;
    private String appliesTo;

    public PolicyHandlerTuple(ServiceDeclaration declaration,
                              String handlerClassName,
                              QName providedIntentName,
                              String policyModelClassName,
                              String appliesTo) {
        this.declaration = declaration;
        this.policyHandlerClassName = handlerClassName;
        this.providedIntentName = providedIntentName;
        this.policyModelClassName = policyModelClassName;
        this.appliesTo = appliesTo;
    }

    public ServiceDeclaration getDeclaration() {
        return declaration;
    }

    public void setDeclaration(ServiceDeclaration declaration) {
        this.declaration = declaration;
    }

    public String getAppliesTo() {
        return appliesTo;
    }

    public void setAppliesTo(String appliesTo) {
        this.appliesTo = appliesTo;
    }

    
    public String getPolicyHandlerClassName() {
        return policyHandlerClassName;
    }
    public void setPolicyHandlerClassName(String policyHandlerClassName) {
        this.policyHandlerClassName = policyHandlerClassName;
    }
    public String getPolicyModelClassName() {
        return policyModelClassName;
    }
    public void setPolicyModelClassName(String policyModelClassName) {
        this.policyModelClassName = policyModelClassName;
    }
    public QName getProvidedIntentName() {
        return providedIntentName;
    }
    public void setProvidedIntentName(QName providedIntentName) {
        this.providedIntentName = providedIntentName;
    }

    @Override
    public String toString() {
        return policyHandlerClassName + ", " + providedIntentName + ", " + policyModelClassName + ", " + appliesTo;
    }
    
}
