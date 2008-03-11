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

package calculator;

/**
 * @version $Rev$ $Date$
 */
public interface PolicyQNames {
    public static final String QUALIFIER = ".";
    
    public static final String POLICY_ITEST_PREFIX="{http://itest/policy}";
    public static final String TEST_INTENT_ONE = POLICY_ITEST_PREFIX + "TestIntent_1";
    public static final String TEST_INTENT_TWO = POLICY_ITEST_PREFIX + "TestIntent_2";
    public static final String TEST_INTENT_THREE = POLICY_ITEST_PREFIX + "TestIntent_3";
    public static final String TEST_INTENT_FOUR = POLICY_ITEST_PREFIX + "TestIntent_4";
    public static final String TEST_INTENT_FIVE = POLICY_ITEST_PREFIX + "TestIntent_5";
    
    public static final String QUALIFIER_ONE = "Qualifier_1";
    
    public static final String QUALIFIED_TEST_INTENT_FOUR = 
        TEST_INTENT_FOUR + QUALIFIER + QUALIFIER_ONE;
    
    public static final String QUALIFIED_TEST_INTENT_ONE = 
        TEST_INTENT_ONE + QUALIFIER + QUALIFIER_ONE;
    
}
