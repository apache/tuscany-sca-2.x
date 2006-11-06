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
package org.apache.tuscany.service.persistence.common;

public class JpaConstants {

    public static final String PROPERTY_VALUE = "value";

    public static final String PROPERTY_NAME = "name";

    public static final String PROPERTY = "//persistence-unit/properties/property";

    public static final String TRANSACTION_TYPE = "//persistence-unit/@transaction-type";

    public static final String NAME = "//persistence-unit/@name";

    public static final String PROVIDER = "//persistence-unit/provider";

    public static final String NON_JTA_DATA_SOURCE = "//persistence-unit/non-jta-data-source";

    public static final String MAPPING_FILE = "//persistence-unit/mapping-file";

    public static final String CLASS = "//persistence-unit/class";

    public static final String JTA_DATA_SOURCE = "//persistence-unit/jta-data-source";

    public static final String JAR_FILE = "//persistence-unit/jar-file";

    public static final String EXCLUDE_UNLISTED_CLASSES = "//persistence-unit/exclude-unlisted-classes";

}
