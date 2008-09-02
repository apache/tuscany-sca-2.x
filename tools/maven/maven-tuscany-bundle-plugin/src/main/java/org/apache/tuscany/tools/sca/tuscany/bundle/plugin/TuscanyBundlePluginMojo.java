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
package org.apache.tuscany.tools.sca.tuscany.bundle.plugin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import org.apache.felix.bundleplugin.BundleAllPlugin;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.versioning.OverConstrainedVersionException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.artifact.InvalidDependencyVersionException;
import org.apache.maven.shared.dependency.tree.DependencyTree;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilderException;
import org.apache.maven.shared.osgi.Maven2OsgiConverter;
import org.osgi.framework.Version;

import aQute.lib.header.OSGiHeader;

/**
 * @version $$
 * @goal execute
 * @phase compile
 * @requiresDependencyResolution test
 * @description Generate versioned OSGi bundles corresponding to Tuscany modules and all 3rd party dependencies
 */
public class TuscanyBundlePluginMojo extends BundleAllPlugin {
    /**
     * The maven project
     *
     * @parameter expression="${project}"
     * @required
     */
    private MavenProject project;
    
    /**
     * Directory containing pom.
     *
     * @parameter expression="${basedir}"
     * @required
     */
    private File baseDir;
    
    /**
     * Build directory for project
     *
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private String buildDirectory;
    
    /**
     * Output directory where bundles are generated.
     *
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     */
    private File outputDirectory;

    /**
     * Artifact resolver
     * 
     * @component
     */
    private ArtifactResolver artifactResolver;

    /**
     * Artifact factory
     * 
     * @component
     */
    private ArtifactFactory artifactFactory;
    
    /**
     * @component
     */
    private ArtifactMetadataSource artifactMetadataSource;    
    
    /**
     * Dependency tree builder
     * 
     * @component
     */
    private DependencyTreeBuilder dependencyTreeBuilder;

    /**
     * @component
     */
    private ArtifactCollector collector;

    /**
     * @component
     */
    private MavenProjectBuilder mavenProjectBuilder;    

    /**
     * The local repository
     *
     * @parameter expression="${localRepository}"
     * @required
     */
    private ArtifactRepository localRepository;

    /**
     * The remote repositories
     *
     * @parameter expression="${project.remoteArtifactRepositories}"
     */
    private List remoteRepositories;
    
    /**
     * @component
     */
    private Maven2OsgiConverter maven2OsgiConverter;
    
    private static final HashSet<String> dependenciesToIgnore = new HashSet<String>();
    private static final HashSet<String> importDirectives = new HashSet<String>();
    private static final HashSet<String> exportDirectives = new HashSet<String>();
    private static final Hashtable<String, String> privatePackages = new Hashtable<String, String>();
    private static final Hashtable<String, String> dynamicImports = new Hashtable<String, String>();
    
    static {
        importDirectives.add("resolution");
        
        exportDirectives.add("uses");
        exportDirectives.add("mandatory");
        exportDirectives.add("include");
        exportDirectives.add("exclude");
        
        dependenciesToIgnore.add("xml-apis:xml-apis");
        dependenciesToIgnore.add("stax:stax-api");
        
        
        // Comma separated list of packages
        privatePackages.put("org.apache.woden:woden", "javax.xml.namespace");
        privatePackages.put("org.apache.xmlbeans:xmlbeans", "org.w3c.dom");
        privatePackages.put("org.apache.axis2:axis2-adb", "org.apache.axis2.util");
        privatePackages.put("org.apache.axis2:addressing", "org.apache.axis2.addressing");
        privatePackages.put("org.apache.axis2:axis2-kernel", "org.apache.axis2.wsdl");
        privatePackages.put("org.apache.bsf:bsf-all", "org.mozilla.javascript");
        privatePackages.put("org.apache.axis2:axis2-codegen", "org.apache.axis2.wsdl,org.apache.axis2.wsdl.util");
        
        dynamicImports.put("org.apache.ws.commons.axiom:axiom-api", "org.apache.axiom.om.impl.*,org.apache.axiom.soap.impl.*");
        dynamicImports.put("org.apache.bsf:bsf-all", "org.mozilla.*");
        dynamicImports.put("org.apache.santuario:xmlsec", "org.apache.ws.security.*");
    }
    

    @SuppressWarnings("unchecked")
    public void execute() throws MojoExecutionException {
        try {
            
            setBasedir(baseDir);
            setBuildDirectory(buildDirectory);
            setOutputDirectory(outputDirectory);
            setMaven2OsgiConverter(maven2OsgiConverter);
            try {
                Field importField = this.getClass().getSuperclass().getDeclaredField("wrapImportPackage");
                importField.setAccessible(true);
                importField.set(this, "*;resolution:=optional");
            } catch (Exception e2) {
                e2.printStackTrace();
                getLog().error("Could not set import instructions");
            }
            
            
            DependencyTree dependencyTree = dependencyTreeBuilder.buildDependencyTree(project, 
                    localRepository, artifactFactory,
                    artifactMetadataSource, collector );
            
            Hashtable<String, String> duplicateWarnings = new Hashtable<String, String>();
            Hashtable<String, Artifact> artifactsToBundle = new Hashtable<String, Artifact>();
            
            for (Object a : dependencyTree.getArtifacts()) {
                Artifact artifact = (Artifact)a;
                
                if (project.getArtifactId().equals(artifact.getArtifactId()) && project.getGroupId().equals(artifact.getGroupId()))
                    continue;
                
                if (Artifact.SCOPE_SYSTEM.equals(artifact.getScope())||Artifact.SCOPE_TEST.equals(artifact.getScope()))
                        continue;
                
                String id = artifact.getGroupId() + ":" + artifact.getArtifactId();
                if (dependenciesToIgnore.contains(id))
                    continue;
                        
                Artifact old = artifactsToBundle.get(id);
                if (old != null && !old.getVersion().equals(artifact.getVersion())) {
                    String oldVersion = old.getVersion();
                    String thisVersion = artifact.getVersion();
                    if (!thisVersion.equals(duplicateWarnings.get(oldVersion))&&!oldVersion.equals(duplicateWarnings.get(thisVersion))) {
                        getLog().warn("Multiple versions of artifacts : " + old + ", " + artifact);
                        duplicateWarnings.put(oldVersion, thisVersion);
                    }
                        
                }
                
                VersionRange versionRange = artifact.getVersionRange();
                if (versionRange == null)
                    versionRange = VersionRange.createFromVersion(artifact.getVersion());
                
                Artifact dependencyArtifact = artifactFactory.createDependencyArtifact(artifact.getGroupId(), 
                        artifact.getArtifactId(), 
                        versionRange, 
                        artifact.getType(), 
                        artifact.getClassifier(), 
                        artifact.getScope());
                
                try {
                    if (old != null && old.getSelectedVersion().compareTo(artifact.getSelectedVersion()) >= 0)
                            continue;
                    else
                        artifactsToBundle.remove(id);
                } catch (OverConstrainedVersionException e1) {
                    getLog().warn("Could not process maven version for artifact " + artifact);
                    continue;
                }
                
                try {
                    artifactResolver.resolve(dependencyArtifact, remoteRepositories, localRepository);
                } catch (ArtifactResolutionException e) {
                    getLog().warn("Artifact " + artifact + " could not be resolved.");
                } catch (ArtifactNotFoundException e) {
                    getLog().warn("Artifact " + artifact + " could not be found.");
                }

                artifact.setFile(dependencyArtifact.getFile());

                artifactsToBundle.put(id, artifact);
                
            }
            
            bundleArtifacts(artifactsToBundle.values());
            
            
        } catch (DependencyTreeBuilderException e) {
            throw new MojoExecutionException("Could not build dependency tree", e);
        } catch (ProjectBuildingException e) {
            throw new MojoExecutionException("Could not build project for artifact", e);
        } catch (InvalidDependencyVersionException e) {
            throw new MojoExecutionException("Invalid dependency version", e);
        } catch (IOException e) {
            throw new MojoExecutionException("Could not build bundle manifest", e);
        }
    }
    

    private void bundleArtifacts(Collection<Artifact> artifacts) throws ProjectBuildingException, 
            InvalidDependencyVersionException, MojoExecutionException, IOException {
        
        for (Artifact artifact : artifacts) {

            artifact.setFile(getFile( artifact ));
            
            MavenProject bundleProject;
            try {
                bundleProject =
                    mavenProjectBuilder.buildFromRepository(artifact, remoteRepositories, localRepository, true);
            } catch (Exception e) {
                getLog().error(e);
                continue;
            }
            bundleProject.setArtifact(artifact);
            
            if ( bundleProject.getDependencyArtifacts() == null ) {
                bundleProject.setDependencyArtifacts(bundleProject.createArtifacts(artifactFactory, null, null ) );
            }
            
            File outputFile = getOutputFile(bundleProject.getArtifact());
            if (outputFile.exists())
                outputFile.delete();
            bundle(bundleProject);
            postProcessBundle(artifact, outputFile);
            
        }
    }
    

    @SuppressWarnings("unchecked")
    private void postProcessBundle(Artifact artifact, File bundleFile) throws IOException {
        
        if (!bundleFile.exists())
            return;
        
        File processedFile = bundleFile;
        boolean retainManifestEntries = false;
        if (!artifact.getGroupId().equals("org.apache.tuscany.sca")) {
          // For pre-bundled 3rd party bundles, retain all OSGi manifest entries except Require-Bundle
          Manifest manifest = getManifest(artifact.getFile());
          if (manifest != null && manifest.getMainAttributes() != null &&
                  manifest.getMainAttributes().getValue("Bundle-SymbolicName") != null) {
              retainManifestEntries = true;
          }
        }
        
        Manifest manifest = getManifest(bundleFile);       
        Attributes attributes = manifest.getMainAttributes();
        
        if (attributes == null) {
            return;
        }

        String artifactId = artifact.getGroupId() + ":" + artifact.getArtifactId();
        
        String bundleSymName = (String)attributes.getValue("Bundle-SymbolicName");
        if (!bundleSymName.startsWith("org.apache.tuscany.sca")) {
            bundleSymName = "org.apache.tuscany.sca.3rdparty." + bundleSymName;
            attributes.putValue("Bundle-SymbolicName", bundleSymName);
            
            processedFile = new File(bundleFile.getParent(), "org.apache.tuscany.sca.3rdparty." + bundleFile.getName());
        }
        
        String imports = (String)attributes.getValue("Import-Package");
        String exports = (String)attributes.getValue("Export-Package");
        
        // For EMF jars 
        if (attributes.getValue("Require-Bundle") != null) {
          attributes.remove(new Attributes.Name("Require-Bundle"));
          attributes.putValue("DynamicImport-Package", "*");
          attributes.remove(new Attributes.Name("Eclipse-LazyStart"));
        }
        
        if (!retainManifestEntries && imports != null) {
            StringBuilder newImportBuf = new StringBuilder();
            Map importMap = OSGiHeader.parseHeader(imports);
            for (Object pkg : importMap.keySet()) {
                
                if (isPrivatePackage(artifactId, (String)pkg)) {
                    continue;
                }
                
                Map importAttr = (Map)importMap.get(pkg);
                String version = (String)importAttr.get("version");
                if (version != null && version.indexOf(',') == -1) {
                    if (((String)pkg).startsWith("org.osgi")) {
                        // Leave version as is - for OSGi packages, assume backward compatibility
                    }
                    else if (!version.matches(".*\\..*\\.")) {
                        Version curVersion = new Version(version);
                        Version nextVersion = new Version(curVersion.getMajor(), curVersion.getMinor()+1, 0);
                        version = '[' + version + ',' + nextVersion + ')';
                    }
                    else
                      version = '[' + version + ',' + version + ']';
                    importAttr.put("version", version);
                }
                updateManifestAttribute((String)pkg, importAttr, importDirectives, newImportBuf);
                
            }
            attributes.putValue("Import-Package", newImportBuf.toString());
        }

        if (!retainManifestEntries && exports != null) {
            StringBuilder newExportBuf = new StringBuilder();
            Map exportMap = OSGiHeader.parseHeader(exports);
            
            for (Object value : exportMap.keySet()) {
                String pkg = (String)value;
                if (!isPrivatePackage(artifactId, pkg)) {
                    Map exportAttr = (Map)exportMap.get(pkg);
                    updateManifestAttribute((String)pkg, exportAttr, exportDirectives, newExportBuf);
                }
            }
            if (newExportBuf.length() > 0)
                attributes.putValue("Export-Package", newExportBuf.toString());
            else
                attributes.remove(new Attributes.Name("Export-Package"));
        }
        
        String dynImport = dynamicImports.get(artifactId);
        if (dynImport != null)
            attributes.putValue("DynamicImport-Package", dynImport);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JarOutputStream jarOut = new JarOutputStream(out, manifest);
        copyJar(bundleFile, jarOut);
        jarOut.close();
        out.close();
        bundleFile.delete();
        FileOutputStream fileOut = new FileOutputStream(processedFile);
        fileOut.write(out.toByteArray());
        fileOut.close();
        
    }
    

    private void copyJar(File file, JarOutputStream jarOut) throws IOException {
        
        try {
            JarInputStream jarIn = new JarInputStream(new FileInputStream(file));
            ZipEntry ze;
            byte[] readBuf = new byte[1000];
            int bytesRead;
            while ((ze = jarIn.getNextEntry()) != null) {
                if (ze.getName().equals("META-INF/MANIFEST.MF"))
                    continue;
                jarOut.putNextEntry(ze);
                while ((bytesRead = jarIn.read(readBuf)) > 0) {
                    jarOut.write(readBuf, 0, bytesRead);
                }
            }
            jarIn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private Manifest getManifest(File jar) {
        try {
            JarInputStream jarIn = new JarInputStream(new FileInputStream(jar));
            Manifest manifest = jarIn.getManifest();
            if (manifest == null) {
                ZipEntry ze;
                while ((ze = jarIn.getNextEntry()) != null) {
                    if (ze.getName().equals("META-INF/MANIFEST.MF"))
                        break;
                }
                if (ze != null) {
                    byte[] bytes = new byte[(int)ze.getSize()];
                    jarIn.read(bytes);
                    manifest = new Manifest(new ByteArrayInputStream(bytes));
                }
            }
            jarIn.close();
            return manifest;
        } catch (IOException e) {
            return null;
        }
    }
    
    private boolean isPrivatePackage(String artifactId, String pkg) {
        String privatePkgs = privatePackages.get(artifactId);
        if (privatePkgs != null) {
            String[] pkgs = privatePkgs.split(",");
            for (int i = 0; i < pkgs.length; i++) {
                if (pkgs[i].trim().equals(pkg))
                    return true;
            }
        }
        return false;
    }
    
  
    private void updateManifestAttribute(String pkg, Map newMap, Set<String> directives, StringBuilder newAttrBuffer) {
        if (newAttrBuffer.length() != 0) newAttrBuffer.append(',');
        newAttrBuffer.append(pkg);
        if (newMap.size() > 0) {
            for (Object attrName : newMap.keySet()) {
                newAttrBuffer.append(';');
                newAttrBuffer.append(attrName);
                if (directives.contains(attrName))
                    newAttrBuffer.append(":=");
                else
                    newAttrBuffer.append('=');
                newAttrBuffer.append('\"');
                newAttrBuffer.append(newMap.get(attrName));
                newAttrBuffer.append('\"');
            }
        }
    }
}
