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

package org.apache.tuscany.sca.contribution.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.impl.ExtensibleImpl;
import org.apache.tuscany.sca.contribution.ContributionMetadata;
import org.apache.tuscany.sca.contribution.Export;
import org.apache.tuscany.sca.contribution.Import;

/**
 * The representation of a deployed contribution
 *
 * @version $Rev$ $Date$
 */
class ContributionMetadataImpl extends ExtensibleImpl implements ContributionMetadata {
    private boolean unresolved;
    private List<Export> exports = new ArrayList<Export>();
    private List<Import> imports = new ArrayList<Import>();
    private List<Composite> deployables = new ArrayList<Composite>();

    ContributionMetadataImpl() {
    }

    public boolean isUnresolved() {
        return unresolved;
    }

    public void setUnresolved(boolean unresolved) {
        this.unresolved = unresolved;
    }

    public List<Export> getExports() {
        return exports;
    }

    public List<Import> getImports() {
        return imports;
    }

    public List<Composite> getDeployables() {
        return deployables;
    }

}
