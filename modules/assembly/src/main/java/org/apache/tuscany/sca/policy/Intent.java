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

import javax.xml.namespace.QName;

/**
 * Represents a policy intent. See the Policy Framework specification for a
 * description of this element.
 *
 * @version $Rev$ $Date$
 * @tuscany.spi.extension.asclient
 */
public interface Intent {
    enum Type {
        interaction, implementation
    };

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
     * Returns the list of SCA constructs that this intent is meant to
     * configure.
     * 
     * @return the list of SCA constructs that this intent is meant to configure
     */
    List<ExtensionType> getConstrainedTypes();

    /**
     * Return a list of required intents
     * @return The list of required intents
     */
    List<Intent> getRequiredIntents();

    /**
     * Returns the list of intents which are mutually exclusive with this intent.
     * 
     * @return the list of mutually exclusive intents.
     */
    List<Intent> getExcludedIntents();

    /**
     * Returns the list of qualified intents.  
     * 
     * @return the list of qualified intents.
     */
    List<Intent> getQualifiedIntents();

    /**
     * Get the default qualified intent
     * 
     * @return
     */
    Intent getDefaultQualifiedIntent();

    /**
     * Set the default qualified intent
     * 
     * @param qualifiedIntent
     */
    void setDefaultQualifiedIntent(Intent qualifiedIntent);

    /**
     * Get the intent type: Interaction or Implementation
     * @return 
     */
    Type getType();

    /**
     * Set the intent type 
     * @param type: Interaction or Implementation
     */
    void setType(Type type);

    /**
     * If this attribute is present and has a value of true it indicates that 
     * the qualified intents defined for this intent are mutually exclusive
     * 
     * @return
     */
    boolean isMutuallyExclusive();

    /**
     * Set the value of mutuallyExclusive  
     * @param mutuallyExclusive
     */
    void setMutuallyExclusive(boolean mutuallyExclusive);

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

    /**
     * Get the parent intent for a qualified intent. If an intent is not qualified,
     * return null.
     * @return The parent intent or null if this intent is not qualified
     */
    Intent getQualifiableIntent();

    /**
     * Set the parent intent for a qualified intent
     * @param intent
     */
    void setQualifiableIntent(Intent intent);

    boolean isUnresolved();

    /**
     * Sets whether the model element is unresolved.
     * 
     * @param unresolved whether the model element is unresolved
     */
    void setUnresolved(boolean unresolved);

}
