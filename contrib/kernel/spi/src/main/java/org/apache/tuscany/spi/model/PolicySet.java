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
import java.util.Collection;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Collections.unmodifiableList;
import java.util.List;
import javax.xml.namespace.QName;

/**
 * Model representation for PolicySet.
 * <p/>
 * $Rev$ $Date$
 */
public class PolicySet extends PolicyContentModel {

    /**
     * QNames of artifacts to which this Policy can apply
     */
    protected List<QName> appliedArtifacts = new ArrayList<QName>();

    /**
     * IntentMap contained in this PolicySet
     */
    private Collection<IntentMap> intentMaps = new ArrayList<IntentMap>();

    /**
     * Name for PolicySet, corresponding to name attribute of PolicySet element in SCDL
     */
    private QName name;

    /**
     * References to other PolicySet
     */
    private Collection<PolicySetReference> policySetReferences = new ArrayList<PolicySetReference>();

    /**
     * Name of intents provided by this PolicySet
     */
    private Collection<IntentName> provideIntents = new ArrayList<IntentName>();

    public PolicySet(QName name, List<IntentName> providesIntent) {
        super();
        this.name = name;
        this.provideIntents.addAll(providesIntent);
    }


    public void addPolicySetReference(PolicySetReference ref) {
        policySetReferences.add(ref);
    }

    public void addAppliedArtifacts(QName artifactName) {
        appliedArtifacts.add(artifactName);
    }

    public List<QName> getAppliedArtifacts() {
        return unmodifiableList(appliedArtifacts);
    }

    public void addIntentMap(IntentMap intentMap) {
        intentMaps.add(intentMap);
    }

    public Collection<IntentMap> getIntentMaps() {
        return unmodifiableCollection(intentMaps);
    }

    public QName getName() {
        return name;
    }

    public Collection<PolicySetReference> getPolicySetReferences() {
        return unmodifiableCollection(policySetReferences);
    }

    public Collection<IntentName> getProvideIntents() {
        return unmodifiableCollection(provideIntents);
    }

}
