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

package manager.fixme;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.List;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.service.ContributionListener;
import org.apache.tuscany.sca.contribution.service.ContributionListenerExtensionPoint;
import org.apache.tuscany.sca.contribution.service.ContributionRepository;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;

/**
 * FIXME Remove this later
 * 
 * A hack to help fixup contribution imports.
 * 
 * @version $Rev$ $Date$
 */
public class ContributionImportFixerHack {
    
    private class DummyRepository implements ContributionRepository {
        private List<Contribution> contributions;

        private DummyRepository(List<Contribution> contributions) {
            this.contributions = contributions;
        }
        
        public void addContribution(Contribution contribution) {}
        public URL find(String contribution) { return null; }
        public Contribution getContribution(String uri) { return null; }
        public List<Contribution> getContributions() { return contributions; }
        public URI getDomain() { return null; }
        public List<String> list() { return null; }
        public void remove(String contribution) {}
        public void removeContribution(Contribution contribution) {}
        public URL store(String contribution, URL sourceURL, InputStream contributionStream) throws IOException { return null; }
        public URL store(String contribution, URL sourceURL) throws IOException { return null;}
        public void updateContribution(Contribution contribution) {}
    }
    
    private ContributionListenerExtensionPoint listeners;
    
    public ContributionImportFixerHack(ExtensionPointRegistry extensionPoints) {
        listeners = extensionPoints.getExtensionPoint(ContributionListenerExtensionPoint.class);
    }

    public void fixContributionImports(List<Contribution> contributions) {
        ContributionRepository dummyRepository = new DummyRepository(contributions);
        for (Contribution contribution: contributions) {
            for (ContributionListener listener: listeners.getContributionListeners()) {
                listener.contributionAdded(dummyRepository, contribution);
            }
        }
    }
}
