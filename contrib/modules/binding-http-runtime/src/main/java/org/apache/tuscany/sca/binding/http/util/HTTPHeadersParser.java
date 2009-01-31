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

package org.apache.tuscany.sca.binding.http.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.tuscany.sca.binding.http.HTTPHeader;

public class HTTPHeadersParser {

	/**
	 * Parse http request headers to a map
	 * 
	 * @param request
	 * @return
	 */
	public static List<HTTPHeader> getHeaders(HttpServletRequest request) {
		List<HTTPHeader> headers = new ArrayList<HTTPHeader>();

		Enumeration<?> headerNames =  request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
		    String headerName = (String) headerNames.nextElement();
		    Object headerValue = request.getHeader(headerName);
		    HTTPHeader header = new HTTPHeader(headerName, headerValue);
		    headers.add(header);
		}
		return headers;
	}
}
