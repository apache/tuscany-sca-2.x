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

package org.apache.tuscany.sca.webapp;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import org.apache.tuscany.sca.host.webapp.HotUpdateContextListener;

/**
 * A Servlet to upload a contribution file.
 */
public class ContributionUploaderServlet extends HttpServlet {

    private static final long serialVersionUID = System.currentTimeMillis();

    private File repository;
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        ServletContext servletContext = config.getServletContext();
        repository = new File(servletContext.getRealPath(HotUpdateContextListener.REPOSITORY_FOLDER_NAME));
    }
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Check that we have a file upload request
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (!isMultipart) {
            throw new RuntimeException("Need multipart content");
        }

        // Create a factory for disk-based file items
        FileItemFactory factory = new DiskFileItemFactory();

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);

        try {
            // Parse the request
            List /* FileItem */ items = upload.parseRequest(request);        
            // Process the uploaded items
            Iterator iter = items.iterator();
            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();

                if (!item.isFormField()) {
                    String fileName = item.getName();
                    int index = fileName.lastIndexOf("\\") + 1;
                    String uploadedFileName = repository.getAbsolutePath() + "/" + fileName.substring(index);
                    File uploadedFile = new File(uploadedFileName);
                    item.write(uploadedFile);
                }
            }
        }
        catch(FileUploadException e) {
            throw new RuntimeException(e);
        }
        catch(Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        
        setResponse(response, request);
    }
    
    private void setResponse(HttpServletResponse response, HttpServletRequest request) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Apache Tuscany WebApp Runtime</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h2>Composite file uploaded</h2>");
        int port = request.getServerPort();
        String portSubStr = ((port == -1) ? "" : (":" + request.getServerPort()));
        String backPath = request.getScheme() + "://" + request.getServerName() + portSubStr + request.getContextPath();
        out.println("Go <a href=\"" + backPath + "\">back</a>");
        out.println("</body>");
        out.println("</html>");
    }
}
