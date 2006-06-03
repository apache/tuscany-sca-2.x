/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.spi.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the set of targets configured on a reference
 *
 * @version $Rev$ $Date$
 */
public class ReferenceTarget extends ModelObject {
    private String referenceName;
    private List<URI> targets = new ArrayList<URI>();
    private ReferenceDefinition referenceDefinition;

    public String getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    public List<URI> getTargets() {
        return targets;
    }

    public void addTarget(URI target) {
        targets.add(target);
    }

    public ReferenceDefinition getReference() {
        return referenceDefinition;
    }

    public void setReference(ReferenceDefinition referenceDefinition) {
        this.referenceDefinition = referenceDefinition;
    }

}
