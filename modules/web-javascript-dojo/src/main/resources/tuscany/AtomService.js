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
dojo.provide("tuscany.AtomService");
dojo.declare("tuscany.AtomService", null, {
   url:null,
   contentType: "application/atom+xml" /*application/atom+json*/, 
   contentHandler: "xml" /*json*/,
   
   constructor: function(args){
		//summary:
		//url : Take a string as a url that points to the atom feed
		//contentType : Take the contentType to use when retrieving the atom feed				

		if(args){
		    var argUrl;
		    var argContentType;
		    
		    if( dojo.isArray(args) ) {
		       argUrl = args[0];
		       argContentType = args[1];
		    } else {
		       argUrl = args;
		    }
		    
			//if the arg is a string, we assume it is a url to the atom feed
			if( (dojo.isString(argUrl)) || (argUrl instanceof dojo._Url)){
				if (argUrl instanceof dojo._Url){
					this.url = argUrl + "";
				}else{
					this.url = argUrl;
				}
			}
			
			if(argContentType) {
			   if(dojo.isString(argContentType)) {
			      if (argContentType == "application/atom+xml") {
			        this.contentType = argContentType;
			      	this.contentHandler = "xml";
			      } else if (argContentType == "application/atom+json") {
			        this.contentType = argContentType;
			        this.contentHandler = "json";
			      }
			   }
			}
		}
	},
	
	get: function(id /*string*/) {
		//The parameters to pass to xhrGet, the url, how to handle it, and the callbacks.
		var xhrArgs = {
      		url: this.url + "/" + id,
      		handleAs: this.contentHandler,
      		headers: {"Accept": this.contentType}
    	};
	
		//Call the asynchronous xhrGet
        var deferred = dojo.xhrGet(xhrArgs);
        
        deferred.addErrback(function(error){
      		alert("An unexpected error occurred: " + error);
    	});
        
        return deferred;        	
	},
	
	
	post: function( entry /*entry object*/) {
		//The parameters to pass to xhrGet, the url, how to handle it, and the callbacks.
		var xhrArgs = {
      		url: this.url,
      		handleAs: this.contentHandler,
      		headers: {"Accept": this.contentType,
      		          "Content-Type": this.contentType},
      		postData: entry
    	};
	
		//Call the asynchronous xhrGet
        var deferred = dojo.xhrPost(xhrArgs);
        
        deferred.addErrback(function(error){
      		alert("An unexpected error occurred: " + error);
    	});
        
        return deferred;        			
	},

	put: function(id /*string*/, entry /*entry object*/) {
		//The parameters to pass to xhrGet, the url, how to handle it, and the callbacks.
		var xhrArgs = {
      		url: this.url + "/" + id,
      		handleAs: this.contentHandler,
      		headers: {"Accept": this.contentType,
      		          "Content-Type": this.contentType},
      		postData: entry
    	};
	
		//Call the asynchronous xhrGet
        var deferred = dojo.xhrPut(xhrArgs);
        
        deferred.addErrback(function(error){
      		alert("An unexpected error occurred: " + error);
    	});
        
        return deferred;        		
	},
	
	del: function(id /*string*/) {
		//The parameters to pass to xhrGet, the url, how to handle it, and the callbacks.
		var xhrArgs = {
      		url: this.url + "/" + id,
      		handleAs: this.contentHandler,
      		headers: {"Accept": this.contentType}
    	};
	
		//Call the asynchronous xhrGet
        var deferred = dojo.xhrDelete(xhrArgs);
        
        deferred.addErrback(function(error){
      		alert("An unexpected error occurred: " + error);
    	});
        
        
        return deferred;        		
	}	
});
