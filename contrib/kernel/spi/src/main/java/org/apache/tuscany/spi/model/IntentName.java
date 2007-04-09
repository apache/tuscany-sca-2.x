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

import java.util.Arrays;

/**
 * Model class represents a name of a intent. An intent name has a domain name and one or more qualified names. For
 * example, in "sec.confidentiality/message/body", the domain name is sec, and the qualified names are confidentiality,
 * message and body
 */
@SuppressWarnings({"SerializableHasSerializationMethods"})
public class IntentName implements java.io.Serializable {
    private static final String QUALIFIED_SEPARATOR = "/";
    private static final String DOMAIN_SEPARATOR = ".";
    private static final long serialVersionUID = -7030021353149084879L;

    /**
     * domain of the intent
     */
    private String domain;

    private String[] qualifiedNames;

    /**
     * Construct a IntentName from a string representation.
     *
     * @param intent string representation for a intent.
     */
    public IntentName(String intent) {
        parse(intent);
    }

    /**
     * Construct a IntentName from domain name and qualified names
     *
     * @param domain         domain name of the intent
     * @param qualifiedNames qualified names of the intent
     */
    public IntentName(String domain, String[] qualifiedNames) {
        this.domain = domain;
        this.qualifiedNames = qualifiedNames;
    }

    public String getDomain() {
        return domain;
    }

    public String[] getQualifiedNames() {
        String[] results = new String[qualifiedNames.length];
        System.arraycopy(qualifiedNames, 0, results, 0, qualifiedNames.length);
        return results;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final IntentName other = (IntentName) obj;
        if (domain == null) {
            if (other.domain != null) {
                return false;
            }
        } else if (!domain.equals(other.domain)) {
            return false;
        }
        return Arrays.equals(qualifiedNames, other.qualifiedNames);
    }

    @Override
    public int hashCode() {
        final int PRIME = 17;
        int result = 1;
        result = PRIME * result + ((domain == null) ? 0 : domain.hashCode());
        result = PRIME * result + Arrays.hashCode(qualifiedNames);
        return result;
    }

    private String getName() {
        StringBuilder sbd = new StringBuilder(domain);
        for (int i = 0; i < qualifiedNames.length; i++) {
            if (i == 0) {
                sbd.append(DOMAIN_SEPARATOR);
            } else {
                sbd.append(QUALIFIED_SEPARATOR);
            }
            sbd.append(qualifiedNames[i]);
        }

        return sbd.toString();
    }

    /**
     * Parse a string representation of intent.
     *
     * @param intent string representation of intent
     */
    private void parse(String intent) {
        String iname = validateFormat(intent);
        int domainIdx = iname.indexOf(DOMAIN_SEPARATOR);
        domain = iname.substring(0, domainIdx);
        String qualifNamesStr = iname.substring(domainIdx + 1);
        qualifiedNames = qualifNamesStr.split(QUALIFIED_SEPARATOR);

    }

    private String validateFormat(String intent) {
        // TODO validate and canonicalize intent name
        return intent;
    }

}
