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
 *
 * The JSON-RPC client code is based on Jan-Klaas' JavaScript
 * o lait library (jsolait).
 *
 * $Id: jsonrpc.js,v 1.36.2.3 2006/03/08 15:09:37 mclark Exp $
 *
 * Copyright (c) 2003-2004 Jan-Klaas Kollhof
 * Copyright (c) 2005 Michael Clark, Metaparadigm Pte Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 */

/**
 * Client component wiring API, supporting JSON and ATOM bindings.
 */

var JSONClient = {};

/**
 * Escape a character.
 */
JSONClient.escapeJSONChar = function(c) {
    if(c == "\"" || c == "\\") return "\\" + c;
    if (c == "\b") return "\\b";
    if (c == "\f") return "\\f";
    if (c == "\n") return "\\n";
    if (c == "\r") return "\\r";
    if (c == "\t") return "\\t";
    var hex = c.charCodeAt(0).toString(16);
    if(hex.length == 1) return "\\u000" + hex;
    if(hex.length == 2) return "\\u00" + hex;
    if(hex.length == 3) return "\\u0" + hex;
    return "\\u" + hex;
};

/**
 * Encode a string into JSON format.
 */
JSONClient.escapeJSONString = function(s) {
    // The following should suffice but Safari's regex is broken (doesn't support callback substitutions)
    // return "\"" + s.replace(/([^\u0020-\u007f]|[\\\"])/g, JSONClient.escapeJSONChar) + "\"";

    // Rather inefficient way to do it
    var parts = s.split("");
    for(var i = 0; i < parts.length; i++) {
        var c = parts[i];
        if(c == '"' || c == '\\' || c.charCodeAt(0) < 32 || c.charCodeAt(0) >= 128)
            parts[i] = JSONClient.escapeJSONChar(parts[i]);
    }
    return "\"" + parts.join("") + "\"";
};

/**
 * Marshall objects to JSON format.
 */
JSONClient.toJSON = function(o) {
    if(o == null)
        return "null";
    if(o.constructor == String)
        return JSONClient.escapeJSONString(o);
    if(o.constructor == Number)
        return o.toString();
    if(o.constructor == Boolean)
        return o.toString();
    if(o.constructor == Date)
        return '{javaClass: "java.util.Date", time: ' + o.valueOf() +'}';
    if(o.constructor == Array) {
        var v = [];
        for(var i = 0; i < o.length; i++)
            v.push(JSONClient.toJSON(o[i]));
        return "[" + v.join(", ") + "]";
    }
    var v = [];
    for(attr in o) {
        if(o[attr] == null)
            v.push("\"" + attr + "\": null");
        else if(typeof o[attr] == "function")
            ; // Skip
        else
            v.push(JSONClient.escapeJSONString(attr) + ": " + JSONClient.toJSON(o[attr]));
    }
    return "{" + v.join(", ") + "}";
};

/**
 * Construct an HTTPBindingClient.
 */
function HTTPBindingClient(name, uri) {
    this.name = name;
    this.uri = uri;
    this.apply = this.createApplyMethod();
}

/**
 * JSON-RPC request counter.
 */
HTTPBindingClient.jsonrpcID = 1;

/**
 * HTTPBindingClient implementation
 */

/**
 * Generate client proxy apply method.
 */
HTTPBindingClient.prototype.createApplyMethod = function() {
    var fn = function() {
        var methodName = arguments[0];
        var args = [];
        for(var i = 1; i < arguments.length; i++)
            args.push(arguments[i]);

        var cb = null;
        if (typeof args[args.length - 1] == "function")
            cb = args.pop();

        var req = HTTPBindingClient.makeJSONRequest(methodName, args, cb);
        return fn.client.jsonApply(req);
    };
    fn.client = this;
    return fn;
};

/**
 * Make a JSON-RPC request.
 */
HTTPBindingClient.makeJSONRequest = function(methodName, args, cb) {
    var req = {};
    req.id = HTTPBindingClient.jsonrpcID++;
    if (cb)
        req.cb = cb;
    var obj = {};
    obj.id = req.id;
    obj.method = methodName;
    obj.params = args;
    req.data = JSONClient.toJSON(obj);
    return req;
};

/**
 * Return the JSON result from an XMLHttpRequest.
 */
HTTPBindingClient.jsonResult = function(http) {
    // Get the charset
    function httpCharset(http) {
        try {
            var contentType = http.getResponseHeader("Content-type");
            var parts = contentType.split(/\s*;\s*/);
            for (var i = 0; i < parts.length; i++) {
                if (parts[i].substring(0, 8) == "charset=")
                    return parts[i].substring(8, parts[i].length);
            }
        } catch (e) {}
        return "UTF-8";
    }
    if(!HTTPBindingClient.charset)
        HTTPBindingClient.charset = httpCharset(http);

    // Unmarshall the JSON response
    var obj;
    eval("obj = " + http.responseText);
    if(obj.error)
        throw new HTTPBindingClient.Exception(obj.error.code, obj.error.msg);
    var res = obj.result;
    return res;
};

/**
 * Apply a function remotely using JSON-RPC.
 */
HTTPBindingClient.prototype.jsonApply = function(req) {
    // Connect to the service
    var http = HTTPBindingClient.getHTTPRequest();
    var hascb = req.cb? true : false;
    http.open("POST", this.uri, hascb);
    http.setRequestHeader("Content-type", "application/json-rpc");

    // Construct call back if we have one
    if(hascb) {
        http.onreadystatechange = function() {
            if(http.readyState == 4) {
                // Pass the result or exception
                if(http.status == 200) {
                    var res = null;
                    try {
                        res = HTTPBindingClient.jsonResult(http);
                    } catch(e) {
                        req.cb(null, e);
                    }
                    req.cb(res);
                } else
                    req.cb(null, HTTPBindingClient.Exception(http.status, http.statusText));
            }
        };

        // Send the request
        http.send(req.data);
        return req.id;
    }

    // Send the request and return the result or exception
    http.send(req.data);
    if (http.status == 200)
        return HTTPBindingClient.jsonResult(http);
    throw new HTTPBindingClient.Exception(http.status, http.statusText);
};

/**
 * REST ATOMPub GET method.
 */
HTTPBindingClient.prototype.get = function(id, cb) {
    // Connect to the service
    var http = HTTPBindingClient.getHTTPRequest();
    var hascb = cb? true : false;
    http.open("GET", this.uri + '/' + id, hascb);

    // Construct call back if we have one
    if (hascb) {
        http.onreadystatechange = function() {
            if (http.readyState == 4) {
                // Pass the result or exception
                if (http.status == 200)
                    cb(http.responseText);
                else
                    cb(null, new HTTPBindingClient.Exception(http.status, http.statusText));
            }
        };

        // Send the request
        http.send(null);
        return true;
    }

    // Send the request and return the result or exception
    http.send(null);
    if (http.status == 200)
        return http.responseText;
    throw new HTTPBindingClient.Exception(http.status, http.statusText);
};

/**
 * REST ATOMPub POST method.
 */
HTTPBindingClient.prototype.post = function (entry, cb) {
    // Connect to the service
    var http = HTTPBindingClient.getHTTPRequest();
    var hascb = cb? true : false;
    http.open("POST", this.uri, hascb);
    http.setRequestHeader("Content-Type", "application/atom+xml");

    // Construct call back if we have one
    if (hascb) {
        http.onreadystatechange = function() {
            // Pass the result or exception
            if (http.readyState == 4) {
                if (http.status == 201)
                    cb(http.responseText);
                else
                    cb(null, new HTTPBindingClient.Exception(http.status, http.statusText));
            }
        };
        // Send the request
        http.send(entry);
        return true;
    }

    // Send the request and return the result or exception
    http.send(entry);
    if (http.status == 201)
        return http.responseText;
    throw new HTTPBindingClient.Exception(http.status, http.statusText);
};

/**
 * REST ATOMPub PUT method.
 */
HTTPBindingClient.prototype.put = function (id, entry, cb) {
    // Connect to the service
    var http = HTTPBindingClient.getHTTPRequest();
    var hascb = cb? true : false;
    http.open("PUT", this.uri + '/' + id, hascb);
    http.setRequestHeader("Content-Type", "application/atom+xml");

    // Construct call back if we have one
    if (hascb) {
        http.onreadystatechange = function() {
            if (http.readyState == 4) {
                // Pass any exception
                if (http.status == 200)
                    cb();
                else
                    cb(new HTTPBindingClient.Exception(http.status, http.statusText));
            }
        };
        // Send the request
        http.send(entry);
        return true;
    }

    // Send the request and return any exception
    http.send(entry);
    if (http.status == 200)
        return true;
    throw new HTTPBindingClient.Exception(http.status, http.statusText);
};

/**
 * REST ATOMPub DELETE method.
 */
HTTPBindingClient.prototype.del = function (id, cb) {       
    // Connect to the service
    var http = HTTPBindingClient.getHTTPRequest();
    var hascb = cb? true : false;
    http.open("DELETE", this.uri + '/' + id, hascb);        

    // Construct call back if we have one
    if (cb) {
        http.onreadystatechange = function() {
            if (http.readyState == 4) {
                // Pass any exception
                if (http.status == 200)
                    cb();
                else
                    cb(new HTTPBindingClient.Exception(http.status, http.statusText));
            }
        };
        // Send the request
        http.send(null);
        return true;
    }

    // Send the request and return any exception
    http.send(null);
    if (http.status == 200)
        return true;
    throw new HTTPBindingClient.Exception(http.status, http.statusText);
};

/**
 * HTTPBindingClient exceptions.
 */
HTTPBindingClient.Exception = function(code, message) {
    this.name = "HTTPBindingClientException";
    this.code = code;
    this.message = message;
};

HTTPBindingClient.Exception.prototype = new Error();

HTTPBindingClient.Exception.prototype.toString = function() {
    return this.name + ": " + this.message;
};

/**
 * XMLHttpRequest wrapper.
 */
HTTPBindingClient.msxmlNames = [ "MSXML2.XMLHTTP.5.0", "MSXML2.XMLHTTP.4.0", "MSXML2.XMLHTTP.3.0", "MSXML2.XMLHTTP", "Microsoft.XMLHTTP" ];

HTTPBindingClient.getHTTPRequest = function() {
    if (HTTPBindingClient.httpFactory)
        return HTTPBindingClient.httpFactory();

    // Mozilla XMLHttpRequest
    try {
        HTTPBindingClient.httpFactory = function() {
            return new XMLHttpRequest();
        };
        return HTTPBindingClient.httpFactory();
    } catch(e) {}

    // Microsoft MSXML ActiveX
    for (var i = 0; i < HTTPBindingClient.msxmlNames.length; i++) {
        try {
            HTTPBindingClient.httpFactory = function() {
                return new ActiveXObject(HTTPBindingClient.msxmlNames[i]);
            };
            return HTTPBindingClient.httpFactory();
        } catch (e) {}
    }

    // Can't create XMLHttpRequest
    HTTPBindingClient.httpFactory = null;
    throw new HTTPBindingClient.Exception(0, "Can't create XMLHttpRequest object");
};

/**
 * Public API.
 */

var sca = {};

/**
 * Return a component proxy.
 */
sca.component = function(name) {
    return new HTTPBindingClient(name, '/components/' + name);
};

/**
 * Return a reference proxy.
 */
sca.reference = function(comp, rname) {
    return new HTTPBindingClient(comp.name + '/' + rname, "/references/" + comp.name + "/" + rname);
};

/**
 * Add proxy functions to a reference proxy.
 */
sca.defun = function(ref) {
    function defapply(name) {
        return function() {
            var args = new Array();
            args[0] = name;
            for (i = 0, n = arguments.length; i < n; i++)
                args[i + 1] = arguments[i];
            return this.apply.apply(this, args);
        };
    }

    for (f = 1; f < arguments.length; f++) {
        var fn = arguments[f];
        ref[fn]= defapply(fn);
    }
    return ref;
};

