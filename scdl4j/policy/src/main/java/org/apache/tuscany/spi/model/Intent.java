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
package org.apache.tuscany.spi.model;

import java.util.ArrayList;
import static java.util.Collections.unmodifiableList;
import java.util.List;
import javax.xml.namespace.QName;

/**
 * Model representation for intent. This class is used by intent loader only, other SCA model classes will not reference
 * this class directly.
 *
 * @version $Rev$ $Date$
 */
public class Intent {

    /**
     * name of intent.
     */
    protected IntentName name;

    /**
     * Description for this intent
     */
    protected String description;

    /**
     * QNames of artifacts this intent can apply to
     */
    protected List<QName> appliedArtifacts = new ArrayList<QName>();

    /**
     * intents required by this intent, only useful when this intent is a profile intent
     */
    protected List<IntentName> requriedIntents = new ArrayList<IntentName>();

    /**
     * Create a policy intent.
     *
     * @param name        name of the intent.
     * @param description description of the intent.
     */
    public Intent(IntentName name, String description) {
        this.name = name;
        this.description = description;
    }

    public List<QName> getAppliedArtifacts() {
        return unmodifiableList(appliedArtifacts);
    }

    public String getDescription() {
        return description;
    }

    public IntentName getName() {
        return name;
    }

    public List<IntentName> getRequriedIntents() {
        return unmodifiableList(requriedIntents);
    }

    public void addRequriedIntents(IntentName intent) {
        requriedIntents.add(intent);
    }

    public void addAppliedArtifacts(QName artifactName) {
        appliedArtifacts.add(artifactName);
    }

    public boolean isProfileIntent() {
        return !requriedIntents.isEmpty();
    }
}
