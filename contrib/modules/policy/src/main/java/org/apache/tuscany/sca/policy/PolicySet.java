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

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathExpression;

/**
 * Represents a policy set. See the Policy Framework specification for a
 * description of this element.
 *
 * @version $Rev$ $Date$
 */
public interface PolicySet {

    /**
     * Returns the intent name.
     * 
     * @return the intent name
     */
    QName getName();

    /**
     * Sets the intent name
     * 
     * @param name the intent name
     */
    void setName(QName name);

    /**
     * Returns the list of operations that this policy set applies to.
     * 
     * @return
     */
    //List<Operation> getOperations();
    /**
     * Returns the list of
     * 
     * @return
     */
    List<PolicySet> getReferencedPolicySets();

    /**
     * Returns the list of provided intents
     * 
     * @return
     */
    List<Intent> getProvidedIntents();

    /**
     * Returns the list of concrete policies, either WS-Policy policy
     * attachments, policy references, or policies expressed in another policy
     * language.
     * 
     * @return the list of concrete policies
     */
    List<Object> getPolicies();

    /**
     * Returns true if the model element is unresolved.
     * 
     * @return true if the model element is unresolved.
     */
    boolean isUnresolved();

    /**
     * Sets whether the model element is unresolved.
     * 
     * @param unresolved whether the model element is unresolved
     */
    void setUnresolved(boolean unresolved);

    /**
     * Returns the XPath expression that is to be used to evaluate
     * if this PolicySet applies to specific attachment point
     * 
     * @return the XPath expression
     */
    String getAppliesTo();

    /**
     * Sets the XPath expression that is to be used to evaluate
     * if this PolicySet applies to specific attachment point
     * 
     */
    void setAppliesTo(String xpath);

    /**
     * Returns the policies / policy attachments provided thro intent maps
     * 
     * @return
     */
    Map<Intent, List<Object>> getMappedPolicies();

    /**
     * Gets the XPath expression that is to be used to evaluate
     * the SCA Artifacts that this policyset will always apply to
     * immaterial of an intent declared on the SCA Artifact
     * 
     * @return the XPath expression
     */
    String getAlwaysAppliesTo();

    /**
     * Sets the XPath expression that is to be used to evaluate
     * the SCA Artifacts that this policyset will always apply to
     * immaterial of an intent declared on the SCA Artifact
     * 
     */
    void setAlwaysAppliesTo(String xpath);

    /**
     * Get the XPath expression for the appliesTo attribute
     * @return the XPath expression for the appliesTo attribute
     */
    XPathExpression getAppliesToXPathExpression();

    /**
     * Set the XPath expression for the appliesTo attribute
     * @param xpathExpression the XPath expression for the appliesTo attribute
     */
    void setAppliesToXPathExpression(XPathExpression xpathExpression);

    /**
     * Get the XPath expression for the alwaysAppliesTo attribute
     * @return the XPath expression for the alwaysAppliesTo attribute
     */
    XPathExpression getAlwaysAppliesToXPathExpression();

    /**
     * Set the XPath expression for the alwaysAppliesTo attribute
     * @param xpathExpression the XPath expression for the alwaysAppliesTo attribute
     */
    void setAlwaysAppliesToXPathExpression(XPathExpression xpathExpression);

}
