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
package org.apache.tuscany.spi.policy;

import java.util.Collection;
import javax.xml.namespace.QName;

import org.apache.tuscany.spi.model.Intent;
import org.apache.tuscany.spi.model.IntentName;

/**
 * The registry for intents.
 *
 * @version $Rev$ $Date$
 */
public interface IntentRegistry {

    /**
     * Register a intent.
     *
     * @param intent intent to register
     */
    void register(Intent intent);

    /**
     * Unregister a intent.
     *
     * @param intent intent to unregister
     */
    void unRegister(Intent intent);

    /**
     * Replace the profile intents in intentNameList with the real concrete intent.
     *
     * @param intentNameList intent list that may contains profile intents
     * @param artifact       QName of SCA artifact
     * @return concrete intents list
     */
    Collection<IntentName> inlineProfileIntent(Collection<IntentName> intentNameList, QName artifact);

    /**
     * Whether the intent is appplicable for specified SCA artifact.
     *
     * @param intentName name of intent
     * @param artifact   QName of SCA artifact
     * @return Whether the intent is appplicable for specified SCA artifact
     */
    boolean isApplicable(IntentName intentName, QName artifact);

    /**
     * Get a list including all qualified intents for a qulifiable intent.
     *
     * @param qualifiable qualifiable intent
     * @param artifact    QName of SCA artifact
     * @return list including all qualified intents for a qulifiable intent.
     */
    Collection<IntentName> getQualifiedIntents(IntentName qualifiable, QName artifact);

    /**
     * Whether the intent is a qualified.
     *
     * @param name intent name.
     * @return whether the intent is a qualified
     */
    boolean isQualifiedIntent(IntentName name);

}
