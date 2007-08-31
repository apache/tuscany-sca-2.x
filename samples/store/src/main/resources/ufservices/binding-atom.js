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
	
function AtomClient(uri) {
	
	this.uri=uri;
	
	this.get = function(id, responseFunction) {
		var xhr = this.createXMLHttpRequest();
		xhr.onreadystatechange = function() {
			if (xhr.readyState == 4) {
				if (xhr.status == 200) {
					if (responseFunction != null) responseFunction(xhr.responseXML);
				} else {
					alert("get - Error getting data from the server");
				}
			}
		}
		xhr.open("GET", uri + id, true);
		xhr.send(null);
	}	
	this.post = function (entry, responseFunction) {
		var xhr = this.createXMLHttpRequest();
		xhr.onreadystatechange = function() {
			if (xhr.readyState == 4) {
				if (xhr.status == 201) {
					if (responseFunction != null) responseFunction(xhr.responseXML);
				} else {
					alert("post - Error getting data from the server");
				}
			}
		}
		xhr.open("POST", uri, true);
		xhr.setRequestHeader("Content-Type", "application/atom+xml");
		xhr.send(entry);
	}
	this.put = function (id, entry, responseFunction) {
		var xhr = this.createXMLHttpRequest();
		xhr.onreadystatechange = function() {
			if (xhr.readyState == 4) {
				if (xhr.status == 200) {
					if (responseFunction != null) responseFunction(xhr.responseXML);
				} else {
					alert("put - Error getting data from the server");
				}
			}
		}
		xhr.open("PUT", uri + id, true);
		xhr.setRequestHeader("Content-Type", "application/atom+xml");
		xhr.send(entry);
	}	
	this.delete = function (id, responseFunction) {       
		var xhr = this.createXMLHttpRequest();
		xhr.onreadystatechange = function() {
			if (xhr.readyState == 4) {
				if (xhr.status == 200) {
					if (responseFunction != null) responseFunction();
				} else {
					alert("delete - Error getting data from the server");
				}
			}
		}
		xhr.open("DELETE", uri + id, true);		
		xhr.send(null);
	}
	
	this.createXMLHttpRequest = function () {
		try {return new XMLHttpRequest();} catch(e) {}      
		try {return new ActiveXObject("Msxml2.XMLHTTP");} catch(e) {}
		try {return new ActiveXObject("Microsoft.XMLHTTP");} catch(e) {}
		alert("XML http request not supported");
		return null;
	}
}

bindingatom = "loaded";