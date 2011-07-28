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

var channels = [];
			
function buildRequest(operation, message) {
	return JSON.stringify({
		operation: operation,
		payload: JSON.stringify(message) 
	});
}
			
function sendMessage(port, operation, message) {
    if (!channels[port]) {
        ws = new WebSocket("ws://" + window.location.hostname + ":" + port);
        channels[port] = ws;
        ws.onopen = function() {
            $(document).trigger('' + port);
        }
        ws.onmessage = function (message) {
			var response = eval('(' + message.data + ')');
			eval('Tuscany.WebsocketComponentContext.' + response.operation + '.responseHandler(' + response.payload + ')');
        }
    }
    
	var jsonReq = buildRequest(operation, message);
	
    if (ws.readyState == WebSocket.CONNECTING) {
            $(document).bind('' + port, jsonReq, function(event) {
                ws.send(event.data);
            });
    } else if (ws.readyState == WebSocket.OPEN) {
        ws.send(jsonReq);
    }
}