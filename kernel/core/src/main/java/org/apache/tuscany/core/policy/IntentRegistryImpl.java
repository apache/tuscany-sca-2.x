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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

import org.apache.tuscany.spi.model.Intent;
import org.apache.tuscany.spi.model.IntentName;
import org.apache.tuscany.spi.policy.IntentRegistry;

/**
 * The default implementation of a data intent registry
 */
public class IntentRegistryImpl implements IntentRegistry {
    private final Map<IntentName, IntentEntry> intentRepo = new HashMap<IntentName, IntentEntry>();

    public Collection<IntentName> getQualifiedIntents(final IntentName qualifiable, final QName artifact) {
        List<IntentName> result = new ArrayList<IntentName>();
        for (IntentName intentName : intentRepo.keySet()) {
            if (intentRepo.get(intentName).getAppliedArtifacts().contains(artifact)
                && PolicyHelper.isQualifiedIntentFor(intentName, qualifiable, true)) {
                result.add(intentName);
            }
        }
        return result;
    }

    public Collection<IntentName> inlineProfileIntent(final Collection<IntentName> intentNameList,
                                                      final QName artifact) {

        return getConcretIntentsInternal(intentNameList, artifact);
    }

    private Collection<IntentName> getConcretIntentsInternal(final Collection<IntentName> intentNameList,
                                                             QName artifact) {
        List<IntentName> result = new ArrayList<IntentName>();
        for (IntentName intentName : intentNameList) {
            IntentEntry intentEntry = intentRepo.get(intentName);
            if (!intentEntry.isProfileIntent()) {
                if (intentEntry.getAppliedArtifacts().contains(artifact)) {
                    result.add(intentEntry.getName());
                }
            } else {
                result.addAll(getConcretIntentsInternal(intentEntry.getRequriedIntents(), artifact));
            }
        }
        return result;
    }

    public boolean isApplicable(IntentName intentName, QName artifact) {
        if (intentRepo.containsKey(intentName)) {
            return intentRepo.get(intentName).getAppliedArtifacts().contains(artifact);
        }
        return false;
    }

    public void register(Intent intent) {

        IntentEntry entry = new IntentEntry(intent);
        // if the qualified intents have been registered, make the intent qualifiable(unqualified)
        if (!getQualifiedIntents(intent.getName()).isEmpty()) {
            entry.setQualified(false);
        }
        intentRepo.put(intent.getName(), entry);
        List<IntentName> qualifiables = getAllQualifiableIntent(intent.getName());
        // set qualifiable intent of this intent unqualified
        for (IntentName qualifiable : qualifiables) {
            IntentEntry qualifiableEntry = intentRepo.get(qualifiable);
            qualifiableEntry.setQualified(false);
            for (QName artifact : intent.getAppliedArtifacts()) {
                qualifiableEntry.addAppliedArtifacts(artifact);
            }
        }
    }

    public void unRegister(Intent intent) {
        if (intentRepo.containsKey(intent.getName())) {
            IntentEntry intentEntry = intentRepo.get(intent.getName());
            List<QName> appliedArtifacts = intent.getAppliedArtifacts();
            for (QName artifact : appliedArtifacts) {
                if (intentEntry.getAppliedArtifacts().contains(artifact)) {
                    intentEntry.removeappliedArtifact(artifact);
                }
            }
            if (intentEntry.getAppliedArtifacts().isEmpty()) {
                intentRepo.remove(intent.getName());
            }
        }
    }

    public boolean isQualifiedIntent(IntentName name) {
        IntentEntry intentEntry = intentRepo.get(name);
        return intentEntry.isQualified();
    }

    private List<IntentName> getQualifiedIntents(final IntentName qualifiable) {
        List<IntentName> result = new ArrayList<IntentName>();
        for (IntentName intentName : intentRepo.keySet()) {
            if (PolicyHelper.isQualifiedIntentFor(intentName, qualifiable, true)) {
                result.add(intentName);
            }
        }
        return result;
    }

    private List<IntentName> getAllQualifiableIntent(final IntentName qualified) {

        List<IntentName> result = new ArrayList<IntentName>();
        for (IntentName intentName : intentRepo.keySet()) {
            if (PolicyHelper.isQualifiedIntentFor(qualified, intentName, false)) {
                result.add(intentName);
            }
        }
        return result;
    }

    /**
     * Wrapper class for intent used internally
     */
    private static final class IntentEntry extends Intent {

        /**
         * Whether this intent is qualified, defaults to true
         */
        private boolean isQualified = true;

        private IntentEntry(Intent intent) {
            super(intent.getName(), intent.getDescription());
            appliedArtifacts.addAll(intent.getAppliedArtifacts());
            requriedIntents.addAll(intent.getRequriedIntents());
        }

        public boolean isQualified() {
            return isQualified;
        }

        public void setQualified(boolean isQualified) {
            this.isQualified = isQualified;
        }

        public void removeappliedArtifact(QName artifact) {
            appliedArtifacts.remove(artifact);
        }
    }

}
