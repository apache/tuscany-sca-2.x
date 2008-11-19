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

package org.apache.tuscany.sca.contribution.osgi.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.Export;
import org.apache.tuscany.sca.contribution.Import;
import org.apache.tuscany.sca.contribution.java.JavaExport;
import org.apache.tuscany.sca.contribution.java.JavaImport;
import org.apache.tuscany.sca.contribution.service.ContributionListener;
import org.apache.tuscany.sca.contribution.service.ContributionRepository;
import org.apache.tuscany.sca.osgi.runtime.OSGiRuntime;

/**
 * Namespace Import/Export contribution listener
 * The listener would process all import/export from a given contribution 
 * and initialize the model resolvers properly
 * 
 * @version $Rev$ $Date$
 */
public class OSGiImportExportListener implements ContributionListener {

    private OSGiBundleProcessor bundleProcessor;

    public OSGiImportExportListener() {
        bundleProcessor = new OSGiBundleProcessor();
    }

    /**
     * Initialize the import/export model resolvers
     * Export model resolvers are same as Contribution model resolver
     * Import model resolvers are matched to a specific contribution if a location URI is specified, 
     *    otherwise it try to resolve against all the other contributions
     */
    public void contributionAdded(ContributionRepository repository, Contribution contribution) {

        OSGiRuntime osgiRuntime = null;
        try {
            if (bundleProcessor.installContributionBundle(contribution) == null) {
                return;
            } else {
                osgiRuntime = OSGiRuntime.getRuntime();
            }
        } catch (Exception e) {
            return;
        }

        HashSet<Contribution> bundlesToInstall = new HashSet<Contribution>();
        // Initialize the contribution imports
        for (Import import_ : contribution.getImports()) {
            boolean initialized = false;

            if (import_ instanceof JavaImport) {
                JavaImport javaImport = (JavaImport)import_;
                String packageName = javaImport.getPackage();

                //Find a matching contribution
                if (javaImport.getLocation() != null) {
                    Contribution targetContribution = repository.getContribution(javaImport.getLocation());
                    if (targetContribution != null) {

                        // Find a matching contribution export
                        for (Export export : targetContribution.getExports()) {
                            if (export instanceof JavaExport) {
                                JavaExport javaExport = (JavaExport)export;
                                if (packageName.equals(javaExport.getPackage())) {

                                    if (osgiRuntime.findBundle(targetContribution.getLocation()) == null)
                                        bundlesToInstall.add(targetContribution);

                                    initialized = true;

                                }
                            }
                            if (initialized)
                                break;
                        }
                    }
                }
            }
            if (!initialized) {
                for (Contribution c : repository.getContributions()) {

                    // Go over all exports in the contribution
                    for (Export export : c.getExports()) {
                        // If the export matches our namespace, try to the resolve the model object
                        if (import_.match(export) && osgiRuntime.findBundle(c.getLocation()) == null) {
                            bundlesToInstall.add(c);
                        }
                    }
                }
            }
        }
        for (Contribution c : bundlesToInstall) {
            try {
                installDummyBundle(osgiRuntime, c);
            } catch (Exception e) {
            }
        }

    }

    public void contributionRemoved(ContributionRepository repository, Contribution contribution) {

    }

    public void contributionUpdated(ContributionRepository repository,
                                    Contribution oldContribution,
                                    Contribution contribution) {

    }

    private void installDummyBundle(OSGiRuntime osgiRuntime, Contribution contribution) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        String EOL = System.getProperty("line.separator");

        String bundleName = contribution.getURI();
        URL contribURL = new URL(contribution.getLocation());
        String contribName = contribURL.getPath();
        if (contribName.endsWith("/")) 
            contribName = contribName.substring(0, contribName.length()-1);
        if (contribName.lastIndexOf("/") >= 0)
            contribName = contribName.substring(contribName.lastIndexOf("/")+1);            

        StringBuffer exportPackageNames = new StringBuffer();
        for (Export export : contribution.getExports()) {
            if (export instanceof JavaExport) {
                if (exportPackageNames.length() > 0)
                    exportPackageNames.append(",");
                exportPackageNames.append(((JavaExport)export).getPackage());
            }
        }
        StringBuffer importPackageNames = new StringBuffer();
        for (Import import_ : contribution.getImports()) {
            if (import_ instanceof JavaImport) {
                if (importPackageNames.length() > 0)
                    importPackageNames.append(",");
                importPackageNames.append(((JavaImport)import_).getPackage());
            }
        }

        String manifestStr =
            "Manifest-Version: 1.0" + EOL
                + "Bundle-ManifestVersion: 2"
                + EOL
                + "Bundle-Name: "
                + bundleName
                + EOL
                + "Bundle-SymbolicName: "
                + bundleName
                + EOL
                + "Bundle-Version: "
                + "1.0.0"
                + EOL
                + "Bundle-Localization: plugin"
                + EOL;

        StringBuilder manifestBuf = new StringBuilder();
        manifestBuf.append(manifestStr);
        manifestBuf.append("Export-Package: " + exportPackageNames + EOL);
        manifestBuf.append("Import-Package: " + importPackageNames + EOL);
        manifestBuf.append("Bundle-ClassPath: .," + contribName + EOL);

        ByteArrayInputStream manifestStream = new ByteArrayInputStream(manifestBuf.toString().getBytes());
        Manifest manifest = new Manifest();
        manifest.read(manifestStream);

        JarOutputStream jarOut = new JarOutputStream(out, manifest);

        ZipEntry ze = new ZipEntry(contribName);
        jarOut.putNextEntry(ze);
        InputStream stream = contribURL.openStream();

        byte[] bytes = new byte[stream.available()];
        stream.read(bytes);
        jarOut.write(bytes);
        stream.close();

        jarOut.close();
        out.close();

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());

        osgiRuntime.installBundle("file://" + bundleName + ".jar", in);

    }

}
