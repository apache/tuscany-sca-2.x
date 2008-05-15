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

package org.apache.tuscany.sca.node.impl;

import java.util.logging.Logger;


/**
 * Some utility methods for the Node implementation
 * 
 * @version $Rev: 556897 $ $Date: 2007-09-07 12:41:52 +0100 (Fri, 07 Sep 2007) $
 */
public class SCANodeUtil {
	private static final Logger logger = Logger.getLogger(SCANodeUtil.class.getName());
	
    /**
     * Given a contribution path an array of composite names or neither this method finds 
     * a suitable contribution to load
     * 
     * @param classLoader
     * @param compositePath
     * @param composites
     * @return the contribution URL
     * @throws MalformedURLException
     */
/*
    public static URL findContributionURLFromCompositeNameOrPath(ClassLoader classLoader, String contributionPath, String[] composites)
      throws MalformedURLException {
        
        String contributionArtifactPath = null;
        URL contributionArtifactURL = null;
        
        
        if (contributionPath != null && contributionPath.length() > 0) {
            
            //encode spaces as they would cause URISyntaxException
            contributionPath = contributionPath.replace(" ", "%20");
            URI contributionURI = URI.create(contributionPath);
            if (contributionURI.isAbsolute() || composites.length == 0) {
                return new URL(contributionPath);
            } else {
        //        contributionArtifactURL = classLoader.getResource(contributionPath);
        //        if (contributionArtifactURL == null) {
        //            throw new IllegalArgumentException("Composite not found: " + contributionArtifactPath);
        //        }
            }
        }

        if ( contributionArtifactURL == null){
            if (composites != null && composites.length > 0 && composites[0].length() > 0) {
        
                // Here the SCADomain was started with a reference to a composite file
                contributionArtifactPath = composites[0];
                contributionArtifactURL = classLoader.getResource(contributionArtifactPath);
                if (contributionArtifactURL == null) {
                    throw new IllegalArgumentException("Composite not found: " + contributionArtifactPath);
                }
            } else {
        
                // Here the SCANode was started without any reference to a composite file
                // We are going to look for an sca-contribution.xml or sca-contribution-generated.xml
        
                // Look for META-INF/sca-contribution.xml
                contributionArtifactPath = Contribution.SCA_CONTRIBUTION_META;
                contributionArtifactURL = classLoader.getResource(contributionArtifactPath);
        
                // Look for META-INF/sca-contribution-generated.xml
                if (contributionArtifactURL == null) {
                    contributionArtifactPath = Contribution.SCA_CONTRIBUTION_GENERATED_META;
                    contributionArtifactURL = classLoader.getResource(contributionArtifactPath);
                }
        
                // Look for META-INF/sca-deployables directory
                if (contributionArtifactURL == null) {
                    contributionArtifactPath = Contribution.SCA_CONTRIBUTION_DEPLOYABLES;
                    contributionArtifactURL = classLoader.getResource(contributionArtifactPath);
                }
            }
        }
    
        if (contributionArtifactURL == null) {
            throw new IllegalArgumentException("Can't determine contribution deployables. Either specify a composite file, or use an sca-contribution.xml file to specify the deployables.");
        }
    
        URL contributionURL = null;
        // "jar:file://....../something.jar!/a/b/c/app.composite"
        try {
            String url = contributionArtifactURL.toExternalForm();
            String protocol = contributionArtifactURL.getProtocol();
            if ("file".equals(protocol)) {
                // directory contribution
                if (url.endsWith(contributionArtifactPath)) {
                    String location = url.substring(0, url.lastIndexOf(contributionArtifactPath));
                    // workaround from evil url/uri form maven
                    contributionURL = FileHelper.toFile(new URL(location)).toURI().toURL();
                }
    
            } else if ("jar".equals(protocol)) {
                // jar contribution
                String location = url.substring(4, url.lastIndexOf("!/"));
                // workaround for evil url/uri from maven
                contributionURL = FileHelper.toFile(new URL(location)).toURI().toURL();
            }
        } catch (MalformedURLException mfe) {
            throw new IllegalArgumentException(mfe);
        }
    
        return contributionURL;
    }	
*/   
    /** 
     * A rather ugly method to find and fix the URL of the service, assuming that there
     * is one. 
     *  
     * we can't get this out of a service reference
     * the component itself doesn't know how to get it  
     * the binding can't to do it automatically as it's not the sca binding
     * 
     * TODO - This would be better done by passing out a Serializable reference to service discovery 
     *         but this doesn't work yet     
     * 
     * @return node manager URL
     */
/*	
    public static void fixUpNodeServiceUrls(List<Component> nodeComponents, URL nodeUrlString)
      throws MalformedURLException, UnknownHostException, IOException {
      
        for(Component component : nodeComponents){
            for (ComponentService service : component.getServices() ){
                for (Binding binding : service.getBindings() ) {
                    fixUpNodeServiceBindingUrl(binding, nodeUrlString);  
                }
            }            
        }
    }   
*/
    /**
     * Find and return the URL of the NodeManagerService
     * 
     * @param nodeComponents
     * @return
     */
/*
    public static String getNodeManagerServiceUrl(List<Component> nodeComponents){
        String nodeManagerUrl = null;
              
        for(Component component : nodeComponents){
            for (ComponentService service : component.getServices() ){
                
                if ( service.getName().equals("NodeManagerService")) {
                    nodeManagerUrl = service.getBindings().get(0).getURI();
                }
            }            
        }
        
        return nodeManagerUrl;
    } 
*/   
    
    /**
     * For node management services that use the HTTP(S) protocol then use the node URL as the enpoint
     * if it has been specified otherwise find a port that isn't in use and make sure the domain name 
     * is the real domain name
     * 
     * @param binding
     * @param nodeURL the URL provided as the identifier of the node
     */
/*
    public static void fixUpNodeServiceBindingUrl(Binding binding, URL manualUrl)
      throws MalformedURLException, UnknownHostException, IOException {

        String urlString = binding.getURI(); 
        
        // only going to fiddle with bindings that use HTTP protocol
        if( (urlString == null) ||
            ((urlString.startsWith("http") != true ) &&
             (urlString.startsWith("https") != true )) ||
            (binding instanceof SCABinding)) {
            return;
        }
        
        URL bindingUrl =  new URL(urlString);
        String originalHost = bindingUrl.getHost();
        String newHost = null;
        int originalPort = bindingUrl.getPort();
        int newPort = 0;
        
        if (manualUrl != null) {
            // the required url has been specified manually
            newHost = manualUrl.getHost();
            newPort = manualUrl.getPort();
            
            if ( newHost.equals("localhost")){
                newHost = InetAddress.getLocalHost().getHostName();
            }
        } else {
            // discover the host and port information
            newHost = InetAddress.getLocalHost().getHostName();
            newPort = findFreePort(originalPort);
        }
        
        // replace the old with the new
        urlString = urlString.replace(String.valueOf(originalPort), String.valueOf(newPort));          
        urlString = urlString.replace(originalHost, newHost);
        
        // set the address back into the NodeManager binding.
        binding.setURI(urlString);   
    }  
*/    
    /**
     * Find a port on this machine that isn't in use. 
     * 
     * @param startPort
     * @return
     */
/*
    public static int findFreePort(int startPort) throws IOException
    {
        ServerSocket socket = new ServerSocket(0);
        int port = socket.getLocalPort();
        socket.close();
        return port;
    }
*/  
    
    /**
     * For node services that have to talk to the domain fix up the reference URL using the 
     * provided domain URL if it has been provided
     * 
     * @param nodeComponents
     * @param domainUrlString
     * @throws MalformedURLException
     * @throws UnknownHostException
     */
/*    
    public static void fixUpNodeReferenceUrls(List<Component> nodeComponents, URL domainUrl)
    throws MalformedURLException, UnknownHostException, ActivationException{
            
      for(Component component : nodeComponents){
          for (ComponentReference reference : component.getReferences() ){
              if ( reference.getName().equals("domainManager") ||
                   reference.getName().equals("scaDomainService")) {
                  for (Binding binding : reference.getBindings() ) {
                      fixUpNodeReferenceBindingUrl(binding, domainUrl);  
                  }
              }
          }            
       }
    }   
*/
    /**
     * For node management references to the domain fix up the binding URLs so that they point
     * to the endpoint described in the domainURL
     * 
     * @param binding
     * @param nodeURL the URL provided as the identifier of the node
     */
/*    
    public static void fixUpNodeReferenceBindingUrl(Binding binding, URL manualUrl)
      throws MalformedURLException, UnknownHostException, ActivationException{

        String urlString = binding.getURI();
        
        // only going to fiddle with bindings that use HTTP protocol
        if( (urlString == null) ||
            ((urlString.startsWith("http") != true ) &&
             (urlString.startsWith("https") != true )) ||
            (binding instanceof SCABinding) ) {
            return;
        }
        
        URL bindingUrl =  new URL(urlString);
        String originalHost = bindingUrl.getHost();
        String newHost = null;
        int originalPort = bindingUrl.getPort();
        int newPort = 0;
        
        if (manualUrl != null) {
            // the required url has been specified manually
            newHost = manualUrl.getHost();
            newPort = manualUrl.getPort();
        } else {
            throw new ActivationException("domain uri can't be null");
        }
        
        // replace the old with the new
        urlString = urlString.replace(String.valueOf(originalPort), String.valueOf(newPort));          
        urlString = urlString.replace(originalHost, newHost);
        
        // set the address back into the NodeManager binding.
        binding.setURI(urlString);   
    } 
    */   
}
