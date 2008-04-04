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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * @version $Rev$ $Date$
 * @goal test
 * @phase integration-test
 * @requiresDependencyResolution test
 * @description Run the unit test over HTTP
 */
public class WebJUnitMojo extends AbstractMojo {
    /**
     * The project to create a build for.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * The test cases to run
     * @parameter 
     */
    private String testCases[];

    /**
     * The URL for the web site
     * @parameter
     */
    private String url;

    /**
     * Timeout for the HTTP connection
     * @parameter
     */
    private int timeout = 300000; // 5 minutes

    /**
     * To avoid throwing exceptions because we want the stop container plugin to be executed
     * @parameter
     */
    private boolean ignoreErrors = true;

    public void execute() throws MojoExecutionException {
        if (project.getPackaging().equals("pom")) {
            return;
        }

        reset();

        if (url == null) {
            url = "http://localhost:8080/" + project.getBuild().getFinalName() + "/junit?op=runAll";
        }

        if (testCases != null) {
            StringBuffer buf = new StringBuffer(url);
            for (int i = 0; i < testCases.length; i++) {
                if (i == 0) {
                    buf.append('?');
                }
                buf.append(testCases[i]);
                if (i != testCases.length - 1) {
                    buf.append(',');
                }
            }
            url = buf.toString();
        }

        getLog().info("Connecting to " + url);

        int runs = 0, errors = 0, failures = 0;
        String xml = "";

        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(url);
            httpget.getParams().setParameter("http.socket.timeout", new Integer(timeout));

            // Execute HTTP request
            HttpResponse response = client.execute(httpget);

            StatusLine status = response.getStatusLine();
            if (status.getStatusCode() != HttpStatus.SC_OK) {
                if (!ignoreErrors) {
                    throw new MojoExecutionException(status.getStatusCode() + ": " + status.getReasonPhrase());
                }
                getLog().error(status.getStatusCode() + ": " + status.getReasonPhrase());
                return;
            }
            Header header = response.getFirstHeader("junit.errors");
            errors = header == null ? 0 : Integer.parseInt(header.getValue());
            header = response.getFirstHeader("junit.failures");
            failures = header == null ? 0 : Integer.parseInt(header.getValue());
            header = response.getFirstHeader("junit.runs");
            runs = header == null ? 0 : Integer.parseInt(header.getValue());
            getLog().info("Runs: " + runs + ", Failures: " + failures + ", Errors: " + errors);

            // Get hold of the response entity
            HttpEntity entity = response.getEntity();

            // If the response does not enclose an entity, there is no need
            // to bother about connection release
            if (entity != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
                try {
                    StringBuffer sb = new StringBuffer();
                    while (true) {
                        String line = reader.readLine();
                        if (line == null) {
                            break;
                        }
                        sb.append(line);
                    }
                    xml = sb.toString();
                    getLog().debug(xml);

                } catch (IOException ex) {

                    // In case of an IOException the connection will be released
                    // back to the connection manager automatically
                    throw ex;

                } catch (RuntimeException ex) {

                    // In case of an unexpected exception you may want to abort
                    // the HTTP request in order to shut down the underlying 
                    // connection and release it back to the connection manager.
                    httpget.abort();
                    throw ex;

                } finally {

                    // Closing the input stream will trigger connection release
                    reader.close();

                }

            }
        } catch (Exception e) {
            if (!ignoreErrors) {
                throw new MojoExecutionException(e.getMessage(), e);
            }
            getLog().error(e);
        }
        if (errors != 0 || failures != 0) {
            if (!ignoreErrors) {
                throw new MojoExecutionException(xml);
            }
            getLog().error(xml);
        }

    }

    /**
     * A workaround to avoid logging conflict with Geronimo
     */
    private static void reset() {
        LogFactory.releaseAll();

        // Restore a reasonable default log impl
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");

        // Make SimpleLog look more like Maven logs
        System.setProperty("org.apache.commons.logging.simplelog.showShortLogname", "false");

        // Restore default Geronimo bootstrap behavior
        System.getProperties().remove("geronimo.bootstrap.logging.enabled");
    }

}
