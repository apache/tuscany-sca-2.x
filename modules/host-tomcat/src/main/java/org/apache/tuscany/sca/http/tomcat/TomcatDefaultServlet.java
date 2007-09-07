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

package org.apache.tuscany.sca.http.tomcat;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.servlets.DefaultServlet;
import org.apache.naming.resources.FileDirContext;
import org.apache.naming.resources.ProxyDirContext;
import org.apache.naming.resources.Resource;

public class TomcatDefaultServlet extends DefaultServlet {
    private static final long serialVersionUID = -7503581551326796573L;
    
    private String documentRoot;
    private ProxyDirContext proxyDirContext;
    
    public TomcatDefaultServlet(String servletPath, String documentRoot) {
        this.documentRoot = documentRoot;
        
        DirContext dirContext = new FileDirContext() {
            
            @Override
            public Attributes getAttributes(String name) throws NamingException {
                return new BasicAttributes();
            }
            
            @Override
            public Object lookup(String name) throws NamingException {
                
                try {
                    final URL url = new URL(TomcatDefaultServlet.this.documentRoot + name);
                    return new Resource() {
                        
                        @Override
                        public InputStream streamContent() throws IOException {
                            return url.openStream();
                        }
                    };
                } catch (MalformedURLException e) {
                    throw new NamingException(e.toString());
                }
            }
        };
        
        proxyDirContext = new ProxyDirContext(new Hashtable(), dirContext);
        resources = proxyDirContext;
    }
    
    @Override
    public void init() throws ServletException {
        super.init();
        resources = proxyDirContext;
    }
    
    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        resources = proxyDirContext;
    }

    @Override
    protected String getRelativePath(HttpServletRequest request) {
        String path = request.getPathInfo();
        if (path == null || path.length() == 0) {
            path = "/";
        }
        return path;
    }
}
