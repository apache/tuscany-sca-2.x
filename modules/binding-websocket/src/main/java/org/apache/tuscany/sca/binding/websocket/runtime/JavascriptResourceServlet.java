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

package org.apache.tuscany.sca.binding.websocket.runtime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet serves the generated javascript service proxies.
 */
public class JavascriptResourceServlet extends HttpServlet {

    private static final String WEBSOCKET_TOOLKIT_PATH = "js/TuscanyWebsocketToolkit.js";

    private String websocketToolkit;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        try {
            resp.setContentType("text/javascript");
            OutputStream os = resp.getOutputStream();
            os.write("var Tuscany = new function() {\n".getBytes());
            os.write(getWebsocketToolkit().getBytes());
            os.write(JavascriptGenerator.getServiceProxies().getBytes());
            os.write("}\n".getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getWebsocketToolkit() {
        if (websocketToolkit == null) {
            websocketToolkit = getResource(WEBSOCKET_TOOLKIT_PATH);
        }
        return websocketToolkit;
    }

    /**
     * Read a resource from the disk by relative path.
     */
    private String getResource(String path) {
        InputStream is = getClass().getClassLoader().getResourceAsStream(path);
        BufferedReader r = new BufferedReader(new InputStreamReader(is));
        StringBuilder builder = new StringBuilder();
        try {
            String line = null;
            while ((line = r.readLine()) != null) {
                builder.append(line + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                r.close();
            } catch (IOException ignored) {
            }
        }
        return builder.toString();
    }

}
