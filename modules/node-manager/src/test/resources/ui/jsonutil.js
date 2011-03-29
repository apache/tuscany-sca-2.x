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

/**
 * JSON data conversion functions.
 */
var json = {};

/**
 * JSON exceptions.
 */
json.Exception = function(code, message) {
    this.name = "JSONException";
    this.code = code;
    this.message = message;
};

json.Exception.prototype = new Error();

json.Exception.prototype.toString = function() {
    return this.name + ": " + this.message;
};

/**
 * Return true if a list represents a JS array.
 */
json.isJSArray = function(l) {
    if (isNil(l))
        return true;
    var v = car(l);
    if (isSymbol(v))
        return false;
    if (isList(v))
        if (!isNil(v) && isSymbol(car(v)))
            return false;
    return true;
};

/**
 * Converts JSON properties to values.
 */
json.jsPropertiesToValues = function(propertiesSoFar, o, i) {
    if (isNil(i))
        return propertiesSoFar;
    var p = car(i);
    var jsv = o[p];
    var v = json.jsValToValue(jsv);

    if (typeof p == 'string') {
        var n = '' + p;
        if (n.slice(0, 1) == '@')
            return json.jsPropertiesToValues(cons(mklist(attribute, "'" + n.slice(1), v), propertiesSoFar), o, cdr(i));
        if (isList(v) && !json.isJSArray(v))
            return json.jsPropertiesToValues(cons(cons(element, cons("'" + n, v)), propertiesSoFar), o, cdr(i));
        return json.jsPropertiesToValues(cons(mklist(element, "'" + n, v), propertiesSoFar), o, cdr(i));
    }
    return json.jsPropertiesToValues(cons(v, propertiesSoFar), o, cdr(i));
};

/**
 * Converts a JSON val to a value.
 */
json.jsValToValue = function(jsv) {
    if (isList(jsv))
        return json.jsPropertiesToValues(mklist(), jsv, reverse(range(0, jsv.length)));
    if (typeof jsv == 'object')
        return json.jsPropertiesToValues(mklist(), jsv, reverse(properties(jsv)));
    if (typeof jsv == 'string')
        return '' + jsv;
    return jsv;
}

/**
 * Return true if a list of strings contains a JSON document.
 */
json.isJSON = function(l) {
    if (isNil(l))
        return false;
    var s = car(l).slice(0, 1);
    return s == "[" || s == "{";
};

/**
 * Convert a list of strings representing a JSON document to a list of values.
 */
json.readJSON = function(l) {
    var s = writeStrings(l);
    var obj;
    eval('obj = { \"val\": ' + s + " }");
    return json.jsValToValue(obj.val);
};

/**
 * Convert a list of values to JSON array elements.
 */
json.valuesToJSElements = function(a, l, i) {
    if (isNil(l))
        return a;
    var pv = json.valueToJSVal(car(l));
    a[i] = pv
    return json.valuesToJSElements(a, cdr(l), i + 1);
};
    
/**
 * Convert a value to a JSON value.
 */
json.valueToJSVal = function(v) {
    if (!isList(v))
        return v;
    if (json.isJSArray(v))
        return json.valuesToJSElements(range(0, v.length), v, 0);
    return json.valuesToJSProperties({}, v);
};

/**
 * Convert a list of values to JSON properties.
 */
json.valuesToJSProperties = function(o, l) {
    if (isNil(l))
        return o;
    var token = car(l);
    if (isTaggedList(token, attribute)) {
        var pv = json.valueToJSVal(attributeValue(token));
        o['@' + attributeName(token).slice(1)] = pv;
    } else if (isTaggedList(token, element)) {
        if (elementHasValue(token)) {
            var pv = json.valueToJSVal(elementValue(token));
            o[elementName(token).slice(1)] = pv;
        } else {
            var child = {};
            o[elementName(token).slice(1)] = child;
            json.valuesToJSProperties(child, elementChildren(token));
        }
    }
    return json.valuesToJSProperties(o, cdr(l));
};

/**
 * Convert a list of values to a list of strings representing a JSON document.
 */
json.writeJSON = function(l) {
    var jsv;
    if (json.isJSArray(l))
        jsv = json.valuesToJSElements(range(0, l.length), l, 0);
    else
        jsv = json.valuesToJSProperties({}, l);
    var s = json.toJSON(jsv);
    return mklist(s);
}

/**
 * Convert a list + params to a JSON-RPC request.
 */
json.jsonRequest = function(id, func, params) {
    var r = mklist(mklist("'id", id), mklist("'method", func), mklist("'params", params));
    return json.writeJSON(valuesToElements(r));
};

/**
 * Convert a value to a JSON-RPC result.
 */
json.jsonResult = function(id, val) {
    return json.writeJSON(valuesToElements(mklist(mklist("'id", id), mklist("'result", val))));
};

/**
 * Convert a JSON-RPC result to a value.
 */
json.jsonResultValue = function(s) {
    var jsres = json.readJSON(s);
    var res = elementsToValues(jsres);
    var val = cadr(assoc("'result", res));
    if (isList(val) && !json.isJSArray(val))
        return mklist(val);
    return val;
};

/**
 * Escape a character.
 */
json.escapeJSONChar = function(c) {
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
json.escapeJSONString = function(s) {
    // The following should suffice but Safari's regex is broken (doesn't support callback substitutions)
    // return "\"" + s.replace(/([^\u0020-\u007f]|[\\\"])/g, json.escapeJSONChar) + "\"";

    // Rather inefficient way to do it
    var parts = s.split("");
    for(var i = 0; i < parts.length; i++) {
        var c = parts[i];
        if(c == '"' || c == '\\' || c.charCodeAt(0) < 32 || c.charCodeAt(0) >= 128)
            parts[i] = json.escapeJSONChar(parts[i]);
    }
    return "\"" + parts.join("") + "\"";
};

/**
 * Marshall objects to JSON format.
 */
json.toJSON = function(o) {
    if(o == null)
        return "null";
    if(o.constructor == String)
        return json.escapeJSONString(o);
    if(o.constructor == Number)
        return o.toString();
    if(o.constructor == Boolean)
        return o.toString();
    if(o.constructor == Date)
        return '{javaClass: "java.util.Date", time: ' + o.valueOf() +'}';
    if(o.constructor == Array) {
        var v = [];
        for(var i = 0; i < o.length; i++)
            v.push(json.toJSON(o[i]));
        return "[" + v.join(", ") + "]";
    }
    var v = [];
    for(attr in o) {
        if(o[attr] == null)
            v.push("\"" + attr + "\": null");
        else if(typeof o[attr] == "function")
            ; // Skip
        else
            v.push(json.escapeJSONString(attr) + ": " + json.toJSON(o[attr]));
    }
    return "{" + v.join(", ") + "}";
};

