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
package org.apache.tuscany.sca.contribution.scanner;

/**
 * An extension point for contribution scanners
 * 
 * @version $Rev$ $Date$
 */
public interface ContributionScannerExtensionPoint {
    
    /**
     * Add a ContributionScanner using the contribution type as the key.
     * 
     * @param scanner The contribution scanner
     */
    void addContributionScanner(ContributionScanner scanner);
    
    /**
     * Remove a ContributionScanner.
     * 
     * @param scanner The contribution scanner
     */
    void removeContributionScanner(ContributionScanner scanner);
    
    /**
     * Returns the ContributionScanner for the given contribution type.
     * 
     * @param contributionType The contribution type
     * @return The contribution scanner
     */
    ContributionScanner getContributionScanner(String contributionType);
    
}