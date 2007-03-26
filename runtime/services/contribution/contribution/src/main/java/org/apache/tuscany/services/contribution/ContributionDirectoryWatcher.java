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

package org.apache.tuscany.services.contribution;

import java.io.File;
import java.io.IOException;

import org.apache.tuscany.services.contribution.spi.ContributionException;
import org.apache.tuscany.services.contribution.spi.ContributionService;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;

@EagerInit
public class ContributionDirectoryWatcher {
    private final String path;

    private final ContributionService contributionService;

    public ContributionDirectoryWatcher(@Reference ContributionService contributionService, 
                                        @Property(name = "path") String path) {
        this.path = path;
        this.contributionService = contributionService;
    }

    @Init
    public void init() {
        File extensionDir = new File(path);
        if (!extensionDir.isDirectory()) {
            // we don't have an extension directory, there's nothing to do
            return;
        }

        File[] files = extensionDir.listFiles();
        for (File file : files) {
            /*
            try {
                if (file.isDirectory()) {
                    this.contributionService.contribute(file.toURL(), false);
                } else {
                    this.contributionService.contribute(file.toURL(), true);
                }
            } catch (ContributionException de) {
                // FIXME handle this
                de.printStackTrace();
            } catch (IOException ioe) {
                // FIXME handle this
                ioe.printStackTrace();
            }
            */
        }
    }
}
