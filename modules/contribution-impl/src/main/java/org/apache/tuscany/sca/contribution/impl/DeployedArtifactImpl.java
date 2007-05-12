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

import org.apache.tuscany.contribution.DeployedArtifact;

/**
 * Representation of a deployed artifact
 *
 * @version $Rev: 527398 $ $Date: 2007-04-10 23:43:31 -0700 (Tue, 10 Apr 2007) $
 */
public class DeployedArtifactImpl extends ArtifactImpl implements DeployedArtifact {
    private Object modelObject;

    protected DeployedArtifactImpl() {
        super();
    }
    
    public Object getModel() {
        return modelObject;
    }
    
    public void setModel(Object modelObject) {
        this.modelObject = modelObject;
    }
}
