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

import javax.xml.xpath.XPathExpression;

/*
 * Represents an external attachment element. See the Policy Framework specification for a
 * description of this element.
 *
 * @version $Rev: 937291 $ $Date: 2010-04-23 06:41:24 -0700 (Fri, 23 Apr 2010) $
 * @tuscany.spi.extension.asclient
 */


public interface ExternalAttachment {
	
	/**
	 * Get the attachTo attribute
	 * @return
	 */
	String getAttachTo();
	
	/**
	 * Set the attachTo attribute
	 * @param name
	 */
	void setAttachTo(String name);
	
	/**
	 * Get the compiled XPath attachTo expression
	 * @return
	 */
	XPathExpression getAttachToXPathExpression();
	
	/**
	 * Set the compiled XPath attachTo expression 
	 * @param expression
	 */
	void setAttachToXPathExpression(XPathExpression expression);
	
	/**
	 * Get the policy sets associated with this ExternalAttachment
	 * @return
	 */
	List<PolicySet> getPolicySets();
	
	/**
	 * Get the intents associated with this ExternalAttachment
	 * @return
	 */
	List<Intent> getIntents();
	
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
}
