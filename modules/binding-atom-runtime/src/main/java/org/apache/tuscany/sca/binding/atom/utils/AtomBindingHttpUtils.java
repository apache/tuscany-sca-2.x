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

package org.apache.tuscany.sca.binding.atom.utils;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tuscany.sca.common.http.HTTPUtils;

public class AtomBindingHttpUtils {
    /**
     * Utility method to set common http headers and other stuff in a
     * default http response. Applications / Extensions can then override and
     * tweak as they see fit.
     *
     * @param response
     */
    public static void prepareHTTPResponse(HttpServletRequest request, HttpServletResponse response) {

        // common http default response values
        HTTPUtils.prepareHTTPResponse(response);

        //set Cache-Control to no-cache to avoid intermediary
        //proxy/reverse-proxy caches and always hit the server
        //that would identify if the value was current or not
        //response.setHeader("Cache-Control", "no-cache");
        //response.setHeader("Expires", new Date(0).toGMTString());

    }

    private static boolean isIE(HttpServletRequest request) {
        String userAgent = request.getHeader("user-agent");

        return (userAgent.indexOf("MSIE") > -1);
    }

}
