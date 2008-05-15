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
package org.apache.tuscany.sca.implementation.script;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.DefaultContributionFactory;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.extension.helper.utils.ResourceHelper;

/**
 * Represents a Script implementation.
 *
 * @version $Rev$ $Date$
 */
public class ScriptImplementation {

    protected String scriptName;
    protected URL scriptURL;
    protected String scriptSrc;
    protected String scriptLanguage;

    public String getScript() {
        return scriptName;
    }

    public void setScript(String scriptName) {
        this.scriptName = scriptName;
    }

    public void setLanguage(String language) {
        this.scriptLanguage = language;
    }

    public void setElementText(String elementText) {
        scriptSrc = elementText;
    }

    public String getScriptLanguage() {
        if (scriptLanguage == null || scriptLanguage.length() < 1) {
            int i = scriptName.lastIndexOf('.');
            if (i > 0) {
                scriptLanguage = scriptName.substring(i + 1);
            }
        }
        return scriptLanguage;
    }

    public String getScriptSrc() {
        if (scriptSrc == null) {
            if (scriptName == null) {
                throw new IllegalArgumentException("script name is null and no inline source used");
            }
            if (scriptURL == null) {
                throw new RuntimeException("No script: " + scriptName);
            }

            scriptSrc = ResourceHelper.readResource(scriptURL);
        }
        return scriptSrc;
    }
    
    public void resolve(ModelResolver resolver) {
    	
    	if (scriptName != null) {
    	    //FIXME The contribution factory should be injected
    	    ContributionFactory contributionFactory = new DefaultContributionFactory();
            Artifact artifact = contributionFactory.createArtifact();
            artifact.setURI(scriptName);
            artifact = resolver.resolveModel(Artifact.class, artifact);
            if (artifact.getLocation() != null) {
                try {
                    scriptURL = new URL(artifact.getLocation());
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
    	}
    }
}
