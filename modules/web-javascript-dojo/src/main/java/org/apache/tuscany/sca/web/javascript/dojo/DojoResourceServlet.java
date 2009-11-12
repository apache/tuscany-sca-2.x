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

package org.apache.tuscany.sca.web.javascript.dojo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * A Resource servlet used to serve dojo files
 *
 * @version $Rev$ $Date$
 */
public class DojoResourceServlet extends HttpServlet {
    private static final long serialVersionUID = -4743631858548812340L;

    public DojoResourceServlet() {

    }


    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = URLDecoder.decode(request.getRequestURI(), "UTF-8");
        
        if( path.startsWith("/dojo")) {
        	//this is a workaround where we need to have dojo files in its own folder
        	//to avoid clean target to clean other non dojo resources
        	path = "dojo" + path;
        } else if( path.startsWith("/")) {
            path = path.substring(1);
        }
        
        // Write the response from the service implementation to the response
        // output stream
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(path);
        if(is != null) {
            OutputStream os = response.getOutputStream(); 
            byte[] buffer = new byte[2048];
            for (;;) {
                int n = is.read(buffer);
                if (n <= 0)
                    break;
                os.write(buffer, 0, n);
            }
            os.flush();
            os.close();            
        }
    }
}
