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
package org.apache.tuscany.core.model.physical.instancefactory;

/**
 * @version $Rev$ $Date$
 */
public class InjectionSiteMapping {
    /**
     * Identifier in SCA terms of the source of the value that will be injected.
     */
    public static class ValueSource {
        public static enum ValueSourceType {
            CALLBACK,
            REFERENCE,
            PROPERTY
        }

        private ValueSourceType valueType;
        private String name;

        public ValueSourceType getValueType() {
            return valueType;
        }

        public void setValueType(ValueSourceType valueType) {
            this.valueType = valueType;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     * Abstraction for a site where a value can be injected.
     */
    public static abstract class Site {
    }

    public static class FieldSite extends Site {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class MethodSite {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class ConstructorSite extends Site {
        private int paramIndex;

        public int getParamIndex() {
            return paramIndex;
        }

        public void setParamIndex(int paramIndex) {
            this.paramIndex = paramIndex;
        }
    }

    private ValueSource source;
    private Site site;

    public ValueSource getSource() {
        return source;
    }

    public void setSource(ValueSource source) {
        this.source = source;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }
}
