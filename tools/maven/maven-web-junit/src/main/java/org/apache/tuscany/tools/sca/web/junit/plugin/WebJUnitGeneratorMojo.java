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
package org.apache.tuscany.tools.sca.web.junit.plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * @version $Rev$ $Date$
 * @goal generate
 * @phase process-resources
 * @requiresDependencyResolution runtime
 * @description Generate the web.xml and geronimo-web.xml
 */
public class WebJUnitGeneratorMojo extends AbstractMojo {
    private final static String ASL_HEADER =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n<!--"
            + "\n   * Licensed to the Apache Software Foundation (ASF) under one"
            + "\n   * or more contributor license agreements.  See the NOTICE file"
            + "\n   * distributed with this work for additional information"
            + "\n   * regarding copyright ownership.  The ASF licenses this file"
            + "\n   * to you under the Apache License, Version 2.0 (the"
            + "\n   * \"License\"); you may not use this file except in compliance"
            + "\n   * with the License.  You may obtain a copy of the License at"
            + "\n   * "
            + "\n   *   http://www.apache.org/licenses/LICENSE-2.0"
            + "\n   * "
            + "\n   * Unless required by applicable law or agreed to in writing,"
            + "\n   * software distributed under the License is distributed on an"
            + "\n   * \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY"
            + "\n   * KIND, either express or implied.  See the License for the"
            + "\n   * specific language governing permissions and limitations"
            + "\n   * under the License.    "
            + "\n-->";

    private final static String GERONIMO_WEB_XML =
        ASL_HEADER + "\n<web-app xmlns=\"http://geronimo.apache.org/xml/ns/j2ee/web-2.0\""
            + "\n    xmlns:d=\"http://geronimo.apache.org/xml/ns/deployment-1.2\">"
            // + "\n     <context-root>${context.root}</context-root>"
            + "\n    <d:environment>"
            + "\n        <d:moduleId>"
            + "\n            <d:groupId>${groupId}</d:groupId>"
            + "\n            <d:artifactId>${artifactId}</d:artifactId>"
            + "\n            <d:version>${version}</d:version>"
            + "\n            <d:type>war</d:type>"
            + "\n        </d:moduleId>"
            + "\n        <d:inverse-classloading />"
            + "\n    </d:environment>"
            + "\n</web-app>\n";

    private final static String WEB_XML =
        ASL_HEADER + "\n<!DOCTYPE web-app PUBLIC \"-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN\" \"http://java.sun.com/dtd/web-app_2_3.dtd\">"
            + "\n<web-app>"
            + "\n   <display-name>${display-name}</display-name>"
            + "\n       <filter>"
            + "\n           <filter-name>tuscany</filter-name>"
            + "\n           <filter-class>org.apache.tuscany.sca.host.webapp.TuscanyServletFilter</filter-class>"
            + "\n       </filter>"
            + "\n       <filter-mapping>"
            + "\n           <filter-name>tuscany</filter-name>"
            + "\n           <url-pattern>/*</url-pattern>"
            + "\n       </filter-mapping>"
            + "\n</web-app>\n";

    /**
     * @parameter
     */
    private boolean geronimo;

    /**
     * The project to create a build for.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    public void execute() throws MojoExecutionException {
        File base =
            new File(project.getBasedir(), "target" + File.separator
                + project.getBuild().getFinalName()
                + File.separator
                + "WEB-INF");
        base.mkdirs();
        File webxml = new File(base, "web.xml");
        getLog().info("Generating " + webxml.toString());
        String content = setParameter(WEB_XML, "display.name", project.getName());
        try {
            FileWriter writer = new FileWriter(webxml);
            writer.append(content);
            writer.close();
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }

        if (geronimo) {
            File geronimoxml = new File(base, "geronimo-web.xml");
            getLog().info("Generating " + geronimoxml.toString());
            content = setParameter(GERONIMO_WEB_XML, "groupId", project.getGroupId());
            content = setParameter(content, "artifactId", project.getArtifactId());
            content = setParameter(content, "version", project.getVersion());
            // content = setParameter(content, "context.root", "/" + project.getBuild().getFinalName());
            try {
                geronimoxml.getParentFile().mkdirs();
                FileWriter writer = new FileWriter(geronimoxml);
                writer.append(content);
                writer.close();
            } catch (IOException e) {
                throw new MojoExecutionException(e.getMessage(), e);
            }
        }

    }

    private String setParameter(String xml, String name, String value) {
        String pattern = "${" + name + "}";
        int index = xml.indexOf(pattern);
        if (index != -1) {
            String content = xml.substring(0, index) + value + xml.substring(index + pattern.length());
            return content;
        }
        return xml;
    }

}
