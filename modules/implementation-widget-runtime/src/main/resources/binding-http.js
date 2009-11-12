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
	
function HTTPClient(uri) {

	this.msxmlNames = [ "MSXML2.XMLHTTP.5.0",
                        "MSXML2.XMLHTTP.4.0",
                        "MSXML2.XMLHTTP.3.0",
                        "MSXML2.XMLHTTP",
                        "Microsoft.XMLHTTP" ];
			            
	this.uri=uri;
	
	this.get = function(id, responseFunction) {
		var xhr = this.createXMLHttpRequest();
		xhr.onreadystatechange = function() {
			if (xhr.readyState == 4) {
				if (xhr.status == 200) {
				    var strDocument = xhr.responseText;
                    if (responseFunction != null) responseFunction(strDocument);
				} else {
                    alert("get - Error getting data from the server");
				}
			}
		}
		xhr.open("GET", uri + '/' + id, true);
		xhr.send(null);
	}	

	this.post = function (entry, responseFunction) {
		var xhr = this.createXMLHttpRequest();
		xhr.onreadystatechange = function() {
			if (xhr.readyState == 4) {
				if (xhr.status == 201) {
				    var strDocument = xhr.responseText;
					if (responseFunction != null) responseFunction(strDocument);
				} else {
					alert("post - Error getting data from the server");
				}
			}
		}
		xhr.open("POST", uri, true);
		xhr.setRequestHeader("Content-Type", "text/xml");
		xhr.send(entry);
	}	

	this.put = function (id, entry, responseFunction) {
		var xhr = this.createXMLHttpRequest();
		xhr.onreadystatechange = function() {
			if (xhr.readyState == 4) {
				if (xhr.status == 200) {
				    var strDocument = xhr.responseText;
					if (responseFunction != null) responseFunction(strDocument);
				} else {
					alert("put - Error getting data from the server");
				}
			}
		}
		xhr.open("PUT", uri + '/' + id, true);
		xhr.setRequestHeader("Content-Type", "text/xml");
		xhr.send(entry);
	}	

	this.del = function (id, responseFunction) {       
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
		xhr.open("DELETE", uri + '/' + id, true);		
		xhr.send(null);
	}
	this.createXMLHttpRequest = function () {
        /* Mozilla XMLHttpRequest */
        try {return new XMLHttpRequest();} catch(e) {}      
		
        /* Microsoft MSXML ActiveX */
        for (var i=0;i < this.msxmlNames.length; i++) {
            try {return new ActiveXObject(this.msxmlNames[i]);} catch (e) {}
        }
        alert("XML http request not supported");
        return null;
	}
}
