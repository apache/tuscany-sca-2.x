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

import org.apache.tuscany.spi.model.IntentName;

/**
 * Contains utility methods for dealing with policies
 */
public final class PolicyHelper {

    private PolicyHelper() {
    }

    /**
     * Whether <code>qualified</code> is qualified intent for <code>qualifiable</code>
     * <p/>
     * For example: sec.confidentiality/message is direct qualifier for sec.confidentiality.
     * sec.confidentiality/message/body is qualifier for sec.confidentiality, but not a direct qualifier
     *
     * @param qualified   qualified intent name
     * @param qualifiable qualifiable intent name
     * @param direct      indicate whether to expect <code>qualified</code> is direct qualified intent for
     *                    <code>qualifiable</code>
     * @return whether <code>qualified</code> is qualified intent for <code>qualifiable</code>
     */
    public static boolean isQualifiedIntentFor(final IntentName qualified,
                                               final IntentName qualifiable,
                                               boolean direct) {
        if (qualified.equals(qualifiable) || !qualified.getDomain().equals(qualifiable.getDomain())) {
            return false;
        }
        boolean result = true;
        String[] shortArray = qualifiable.getQualifiedNames();
        String[] longArray = qualified.getQualifiedNames();
        if (longArray.length - shortArray.length < 1 && !direct) {
            return false;
        } else if (direct && longArray.length - shortArray.length != 1) {
            return false;
        }
        for (int i = 0; i < shortArray.length; i++) {
            if (!shortArray[i].equals(longArray[i])) {
                result = false;
                break;
            }
        }
        return result;
    }
}
