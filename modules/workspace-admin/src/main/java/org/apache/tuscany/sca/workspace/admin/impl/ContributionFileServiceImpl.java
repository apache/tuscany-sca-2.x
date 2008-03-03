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

package org.apache.tuscany.sca.workspace.admin.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Scope;

/**
 * Implementation of a servlet component supporting file upload/download.
 *
 * @version $Rev$ $Date$
 */
@Scope("COMPOSITE")
public class ContributionFileServiceImpl extends HttpServlet {
    private static final long serialVersionUID = -4560385595481971616L;
    
    @Property
    public String directoryName;
    
    private ServletFileUpload upload;
    private File files;
    
    /**
     * Initialize the component.
     */
    @Init
    public void initialize() throws IOException {
        upload = new ServletFileUpload(new DiskFileItemFactory());
        
        files = new File(directoryName);
        if (!files.exists()) {
            files.mkdirs();
        }
    }
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Upload contributions
        try {
            for (FileItem item: (List<FileItem>)upload.parseRequest(request)) {
                if (!item.isFormField()) {
                    item.write(new File(files, item.getName()));
                }
            }
            response.sendRedirect("/ui/files");
        }
        catch (Exception e) {
            throw new IOException(e.toString());
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // Download a contribution file
        String requestURI = URLDecoder.decode(request.getRequestURI(), "UTF-8");
        String path = requestURI.substring(request.getServletPath().length());
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        try {
            URI uri = URI.create(path);
            
            // Default to file protocol
            if (uri.getScheme() == null) {
                uri = new File(files, path).toURI();
            }
            
            // Support the following syntaxes
            // foo.jar!/file.txt
            // directory!/file.txt
            // directory/!/file.txt
            String str = uri.toString();
            int e = str.indexOf("!/"); 
            if (e != -1) {
                int s = str.lastIndexOf('/', e - 2) +1;
                if (str.substring(s, e).contains(".")) {
                    str = "jar:" + str;
                } else {
                    str = str.substring(0, e) + str.substring(e + 1);
                }
                uri = URI.create(str);
            }
            
            // Read the file and write to response 
            InputStream is = uri.toURL().openStream();
            ServletOutputStream os = response.getOutputStream();
            byte[] buffer = new byte[4096];
            for (;;) {
                int n = is.read(buffer);
                if (n < 0) {
                    break;
                }
                os.write(buffer, 0, n);
            }
            is.close();
            os.flush();
            
      } catch (FileNotFoundException e) {
          response.sendError(HttpServletResponse.SC_NOT_FOUND);
      }
    }
    
}
