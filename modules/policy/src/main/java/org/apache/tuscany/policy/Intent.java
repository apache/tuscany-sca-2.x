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
package org.apache.tuscany.policy;

import java.util.List;

import javax.xml.namespace.QName;

import org.apache.tuscany.interfacedef.Operation;

/**
 * Represents a policy intent. See the Policy Framework specification for a
 * description of this element.
 */
public interface Intent {

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
     * Returns the list of operations that this intent applies to.
     * 
     * @return
     */
    List<Operation> getOperations();

    /**
     * Returns the list of SCA constructs that this intent is meant to
     * configure.
     * 
     * @return the list of SCA constructs that this intent is meant to configure
     */
    List<QName> getConstrains();

    /**
     * Returns the list of required intents.
     * 
     * @return
     */
    List<Intent> getRequiredIntents();

    /**
     * Returns the list of children qualified intents.
     * 
     * @return the list of children qualified intents.
     */
    List<Intent> getQualifiedIntents();

    /**
     * Returns the intent description.
     * 
     * @return the intent description
     */
    String getDescription();

    /**
     * Sets the intent description.
     * 
     * @param description the intent description
     */
    void setDescription(String description);

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
