
/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

var SCA = new function() {
	
this.TuscanyComet = {
	appUrl: 'tuscany-comet',
	connectedEndpoint : null,
    sessionId : $.Guid.New(),	
	connect : function(transport) {
		if(transport == null) {
			transport = 'streaming';
		}
		$.atmosphere.subscribe(document.location.toString() + this.appUrl + "/connect?sessionId=" + this.sessionId,
				this.callback, 
				$.atmosphere.request = {
					method : 'GET',
					transport : transport,
					maxRequest: 1000000,
				});
		this.connectedEndpoint = $.atmosphere.response;
	},
	callAsync : function(url, params, callbackMethod) {
		this.connectedEndpoint.push(document.location.toString()
				+ this.appUrl + '/' + url,
				null, 
				$.atmosphere.request = {
					method : 'POST',
					data : 'sessionId=' + this.sessionId + '&callbackMethod=' + callbackMethod.name + '&params=' + params
				});
	},
	callback : function(response) {
		eval(response.responseBody);
	}
};


this.CometComponentContext = new Object();
