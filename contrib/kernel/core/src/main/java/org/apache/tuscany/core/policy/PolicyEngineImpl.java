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
package org.apache.tuscany.core.policy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import javax.xml.namespace.QName;

import org.apache.tuscany.spi.model.IntentMap;
import org.apache.tuscany.spi.model.IntentName;
import org.apache.tuscany.spi.model.PolicyContentModel;
import org.apache.tuscany.spi.model.PolicyModel;
import org.apache.tuscany.spi.model.PolicySet;
import org.apache.tuscany.spi.model.PolicySetReference;
import org.apache.tuscany.spi.model.Qualifier;
import org.apache.tuscany.spi.policy.IntentRegistry;
import org.apache.tuscany.spi.policy.PolicyEngine;
import org.apache.tuscany.spi.policy.PolicySetContainer;
import org.apache.tuscany.spi.policy.SCATypeManager;

/**
 * Default implementation of a polciy engine
 */
public class PolicyEngineImpl implements PolicyEngine {
    private final IntentRegistry intentRegistry;
    private final PolicySetContainer policySetContainer;
    private final SCATypeManager scaTypeManager;

    public PolicyEngineImpl(IntentRegistry intentRegistry,
                            PolicySetContainer policySetContainer,
                            SCATypeManager scaTypeManager) {
        this.intentRegistry = intentRegistry;
        this.policySetContainer = policySetContainer;
        this.scaTypeManager = scaTypeManager;
    }

    @SuppressWarnings("unchecked")
    public Collection<PolicyModel> getPolicy(final IntentName[] requires,
                                             final QName[] policySetNames,
                                             final QName artifactType) {
        if (requires == null || requires.length == 0) {
            return Collections.EMPTY_LIST;
        }
        Collection<PolicyModel> result = new ArrayList<PolicyModel>();
        Collection<IntentName> requiredIntents = java.util.Arrays.asList(requires);
        Collection<IntentName> matchings;

        //handle profile intents
        requiredIntents = intentRegistry.inlineProfileIntent(requiredIntents, artifactType);
        //
        if (policySetNames != null && policySetNames.length != 0) {
            Collection<PolicySet> explicitPolicySet = getExplicitPolicySet(policySetNames);
            matchings = calculateExplicitPolicySet(requiredIntents, explicitPolicySet, artifactType, result);
            //remove satisfied intent
            requiredIntents.removeAll(matchings);
        }
        //
        if (requiredIntents.size() > 0) {
            matchings = findingAdditionalMatching(requiredIntents, artifactType, result);
            requiredIntents.removeAll(matchings);
        }
        //If no collection of policySets covers all required intents, the configuration is not valid.
        if (requiredIntents.size() > 0) {
            //TODO
        }
        return result;
    }


    private boolean introspectPolicySet(PolicySet policySet, IntentName intent, QName artifactType,
                                        Collection<PolicyModel> matchings) {
        Collection<QName> appliedArtifacts = policySet.getAppliedArtifacts();
        boolean provide = false;
        for (QName name : appliedArtifacts) {
            if (this.scaTypeManager.isTypeOf(artifactType, name)) {
                provide = true;
                break;
            }
        }
        if (!provide) {
            return false;
        }
        //1. The required intent matches a provides intent in a policySet exactly.
        if (policySet.getProvideIntents().contains(intent)) {
            if (intentRegistry.isQualifiedIntent(intent)) {
                addMatching(matchings, policySet);
            } else {
                Collection<IntentMap> intentMaps = policySet.getIntentMaps();
                provide = searchIntentMaps(intent, intent, matchings, intentMaps);
                if (provide) {
                    return true;
                }
            }
        } else if (provideAbstract(intent, policySet.getProvideIntents())) {
            // 2. The provides intent is a parent (e.g. prefix) of the required intent (in this case the policySet must
            // have an intentMap entry for the requested qualifier)
            Collection<IntentMap> intentMaps = policySet.getIntentMaps();
            IntentName satisfiedIntent = getSatisfiedIntent(intent, policySet.getProvideIntents());
            provide = searchIntentMaps(intent, satisfiedIntent, matchings, intentMaps);
            if (provide) {
                return true;
            }
        } else if (provideQualifier(intent, policySet.getProvideIntents())) {
            //3.   The provides intent is more qualified than the required intent
            if (intentRegistry.isQualifiedIntent(intent)) {
                addMatching(matchings, policySet);
            } else {
                //TODO
            }
        }

        //handle PolicySetReference
        Collection<PolicySetReference> policySetReferences = policySet.getPolicySetReferences();
        for (PolicySetReference reference : policySetReferences) {
            PolicySet referencedPolicySet = policySetContainer.getPolicySet(reference.getReference());
            if (introspectPolicySet(referencedPolicySet, intent, artifactType, matchings)) {
                return true;
            }
        }

        return false;
    }

    private void addMatching(Collection<PolicyModel> matching, PolicyContentModel policy) {
        if (!policy.getWsPolicyAttachments().isEmpty()) {
            matching.addAll(policy.getWsPolicyAttachments());
        }
        if (!policy.getPolicyExtensions().isEmpty()) {
            matching.addAll(policy.getPolicyExtensions());
        }
    }

    private boolean searchIntentMaps(IntentName require,
                                     IntentName satisfiedIntent,
                                     Collection<PolicyModel> matchings,
                                     Collection<IntentMap> intentMaps) {
        String qualifierName = getQualifierName(require, satisfiedIntent, intentMaps);
        for (IntentMap intentMap : intentMaps) {
            if (intentMap.getProvideIntents().contains(qualifierName)) {
                Collection<Qualifier> qualifiers = intentMap.getQualifiers();
                for (Qualifier qualifier : qualifiers) {
                    String nextQualifier = getNextQualifier(require, satisfiedIntent, intentMap);
                    if (qualifier.getName().equals(nextQualifier)) {
                        if (intentRegistry
                            .isQualifiedIntent(new IntentName(satisfiedIntent.toString() + "/" + nextQualifier))) {
                            addMatching(matchings, qualifier);
                            return true;
                        } else {
                            require = new IntentName(require.toString() + "/" + intentMap.getDefaultProvideIntent());
                            satisfiedIntent = new IntentName(satisfiedIntent.toString() + "/" + qualifierName);
                            intentMaps = new ArrayList<IntentMap>(0);
                            intentMaps.add(qualifier.getIntentMap());
                            searchIntentMaps(require, satisfiedIntent, matchings, intentMaps);
                        }
                        break;
                    }
                }
            }
        }
        return false;
    }

    private String getQualifierName(IntentName require, IntentName satisfiedIntent, Collection<IntentMap> intentMaps) {
        String[] requrieQualifiers = require.getQualifiedNames();
        String[] satisfiedQualifiers = satisfiedIntent.getQualifiedNames();
        if (requrieQualifiers.length == satisfiedQualifiers.length) {
            return requrieQualifiers[requrieQualifiers.length - 1];
        } else if (requrieQualifiers.length > satisfiedQualifiers.length) {
            return satisfiedQualifiers[satisfiedQualifiers.length - 1];
        }
        //TODO raise exception
        return null;
    }

    private String getNextQualifier(IntentName require, IntentName satisfiedIntent, IntentMap intentMap) {
        String[] requrieQualifiers = require.getQualifiedNames();
        String[] satisfiedQualifiers = satisfiedIntent.getQualifiedNames();
        if (requrieQualifiers.length > satisfiedQualifiers.length) {
            return requrieQualifiers[satisfiedQualifiers.length];
        } else {
            return intentMap.getDefaultProvideIntent();
        }
    }

    private IntentName getSatisfiedIntent(IntentName require, Collection<IntentName> provides) {
        for (IntentName name : provides) {
            if (PolicyHelper.isQualifiedIntentFor(require, name, true)) {
                return name;
            }
        }
        //TODO raise exception
        return null;
    }

    private boolean provideAbstract(IntentName require, Collection<IntentName> provides) {
        for (IntentName name : provides) {
            if (PolicyHelper.isQualifiedIntentFor(require, name, true)) {
                return true;
            }
        }
        return false;
    }

    private boolean provideQualifier(IntentName require, Collection<IntentName> provides) {
        for (IntentName name : provides) {
            if (PolicyHelper.isQualifiedIntentFor(name, require, true)) {
                return true;
            }
        }
        return false;
    }

    private Collection<PolicySet> getExplicitPolicySet(QName[] policySetNames) {
        Collection<PolicySet> result = new ArrayList<PolicySet>();
        for (QName policySetName : policySetNames) {
            PolicySet set = policySetContainer.getPolicySet(policySetName);
            if (set != null) {
                result.add(set);
            }
        }
        return result;
    }

    /**
     * Step C. Calculate the list of explicitly specified policySets that apply to the target element as follows: 1.
     * Start with the list of policySets specified in the element's policySet attribute. 2. If any of these explicitly
     * listed policySets has an XPath expression in its appliesTo attribute that does not match the target element
     * (binding or implementation) then the composite is invalid. It does not match if the XPath returns a result set
     * that corresponds to XPath false. For example, a policySet could have appliesTo=”binding.ws/soaphttp”. This would
     * return false if the target element is a <binding.jms…/> element. 3. Include the values of policySet attributes
     * from ancestor elements. 4. Remove any policySet where the XPath expression in that policySet’s appliesTo
     * attribute does not match the target element.
     * <p/>
     *
     * @param requires
     * @param policies
     * @return intent names was satisfied by this step.
     */
    private Collection<IntentName> calculateExplicitPolicySet(Collection<IntentName> requires,
                                                              Collection<PolicySet> policies,
                                                              QName artifactType,
                                                              Collection<PolicyModel> matchings) {
        Collection<IntentName> satisfied = new ArrayList<IntentName>();
        for (IntentName intent : requires) {
            for (PolicySet policySet : policies) {
                if (introspectPolicySet(policySet, intent, artifactType, matchings)) {
                    satisfied.add(intent);
                }
            }
        }
        return satisfied;
    }

    /**
     * * The remaining required intents, if any, are provided by finding additional matching policySets within the SCA
     * system. E. Choose the smallest collection of these policySets that match all remaining required intents. A
     * policySet matches a required intent if any of the following are true: 1. The required intent matches a provides
     * intent in a policySet exactly. 2. The provides intent is a parent (e.g. prefix) of the required intent (in this
     * case the policySet must have an intentMap entry for the requested qualifier) 3. The provides intent is more
     * qualified than the required intent All intents should now be satisfied.
     *
     * @param remainings
     * @param artifactType
     * @param matchings
     */
    private Collection<IntentName> findingAdditionalMatching(final Collection<IntentName> remainings,
                                                             QName artifactType,
                                                             Collection<PolicyModel> matchings) {
        Collection<IntentName> satisfied = new ArrayList<IntentName>();
        Collection<PolicySet> policies = this.policySetContainer.getAllPolicySet();
        for (IntentName intent : remainings) {
            for (PolicySet policySet : policies) {
                if (introspectPolicySet(policySet, intent, artifactType, matchings)) {
                    satisfied.add(intent);
                }
            }
        }
        return satisfied;
    }
}
