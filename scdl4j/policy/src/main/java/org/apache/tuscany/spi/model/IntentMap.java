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

/**
 * Represents an IntentMap within PolicySet.
 *
 * @version $Rev$ $Date$
 */
public class IntentMap extends PolicyModel {

    /*  Name of default intent provied by this IntentMap */
    private String defaultProvideIntent;

    /* Name of intent provided by this IntentMap */
    private Collection<String> provideIntents = new ArrayList<String>();

    /* Qualifiers of this IntentMap */
    private Collection<Qualifier> qualifiers = new ArrayList<Qualifier>();

    public IntentMap(String defaultProvideIntent, Collection<String> provideIntents) {
        super();
        this.defaultProvideIntent = defaultProvideIntent;
        this.provideIntents.addAll(provideIntents);
    }

    public Collection<String> getProvideIntents() {
        return unmodifiableCollection(provideIntents);
    }

    public void addQualifier(Qualifier qualifier) {
        qualifiers.add(qualifier);
    }

    public Collection<Qualifier> getQualifiers() {
        return unmodifiableCollection(qualifiers);
    }

    public String getDefaultProvideIntent() {
        return defaultProvideIntent;
    }


}
