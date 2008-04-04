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
package org.apache.tuscany.tools.sca.dependency.lister.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.settings.Settings;
import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.apache.maven.shared.dependency.tree.DependencyTree;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilderException;

/**
 * @version $Rev: 588816 $ $Date: 2007-10-27 01:22:38 +0100 (Sat, 27 Oct 2007) $
 * @goal execute
 * @phase generate-sources
 * @requiresDependencyResolution test
 * @description List dependencies for an SCA project
 */
public class DependencyListerMojo extends AbstractMojo {
    /**
     * The project to create a build for.
     *
     * @parameter expression="${project}"
     * @required
     */
    private MavenProject project;

    /**
     * Used for resolving artifacts
     *
     * @component
     */
    private ArtifactResolver artifactResolver;

    /**
     * Factory for creating artifact objects
     *
     * @component
     */
    private ArtifactFactory artifactFactory;
    
    /**
     * @component
     */
    private ArtifactMetadataSource artifactMetadataSource;    
    
    /**
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
     * The local repository where the artifacts are located
     *
     * @parameter expression="${localRepository}"
     * @required
     */
    private ArtifactRepository localRepository;

    /**
     * The remote repositories where artifacts are located
     *
     * @parameter expression="${project.remoteArtifactRepositories}"
     */
    private List remoteRepositories;

    /**
     * The current user system settings for use in Maven.
     *
     * @parameter expression="${settings}"
     * @required
     * @readonly
     */
    private Settings settings;

    
    /**
     * The build.xml file to generate.
     * @parameter expression="${basedir}/target/dependency.txt"
     */
    private String buildFile;
    
    public void execute() throws MojoExecutionException {

        System.out.println("Analysing " + buildFile);
        
        // Open the target build.xml file
        File targetFile = new File(buildFile);
        PrintWriter pw;
        try {
            pw = new PrintWriter(new FileOutputStream(targetFile));
        } catch (FileNotFoundException e) {
            System.out.println(e.toString());
            return;
        }

        DependencyTree dependencyTree;

        try
        {
            dependencyTree = dependencyTreeBuilder.buildDependencyTree( project, localRepository, artifactFactory,
                                                                        artifactMetadataSource, collector );
        }
        catch ( DependencyTreeBuilderException e )
        {
            throw new MojoExecutionException( "Can't build dependency tree", e );
        }
        
 //       processNode(dependencyTree.getRootNode(), "--", pw);   
      
        for (Artifact artifact: (List<Artifact>)project.getTestArtifacts()) {
            pw.println( artifact.getFile().getName() + "\t" + 
                        artifact.getScope() + "\t" + 
                        project.getName() + "\t" + 
                        findArtifactPath(dependencyTree.getRootNode(), artifact));
        }          
        
        pw.close();        
    }
    
    private void processNode(DependencyNode node, String indent, PrintWriter pw){
        
        pw.println(indent + node.getArtifact().getScope() + " " + node.getArtifact().toString());
        
        Iterator it = node.getChildren().iterator();
        while ( it.hasNext() )
        {
            DependencyNode child = (DependencyNode) it.next();
            
            processNode(child, indent + "--", pw);
        }
    }
    
    private String findArtifactPath(DependencyNode node, Artifact artifact){
        String path = null;
        
        Iterator it = node.getChildren().iterator();
        while ( it.hasNext() && (path == null) )
        {
            DependencyNode child = (DependencyNode) it.next();
            
            if (child.getArtifact().getArtifactId().equals(artifact.getArtifactId())) {
                path = child.getArtifact().getArtifactId() + "-" + child.getArtifact().getVersion();
            } else {
                path = findArtifactPath(child, artifact);
                
                if (path != null){
                    path = child.getArtifact().getArtifactId() + "-" + child.getArtifact().getVersion() + "\t" + path;
                }
            }
        }
       
        return path;
    }    

}