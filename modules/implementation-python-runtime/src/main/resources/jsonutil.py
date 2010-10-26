#  Licensed to the Apache Software Foundation (ASF) under one
#  or more contributor license agreements.  See the NOTICE file
#  distributed with this work for additional information
#  regarding copyright ownership.  The ASF licenses this file
#  to you under the Apache License, Version 2.0 (the
#  "License"); you may not use this file except in compliance
#  with the License.  You may obtain a copy of the License at
#  
#    http://www.apache.org/licenses/LICENSE-2.0
#    
#  Unless required by applicable law or agreed to in writing,
#  software distributed under the License is distributed on an
#  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
#  KIND, either express or implied.  See the License for the
#  specific language governing permissions and limitations
#  under the License.

# JSON data conversion functions

try:
    import json
except:
    from django.utils import simplejson as json

from StringIO import StringIO
from util import *
from elemutil import *

# Return true if a list represents a JS array
def isJSArray(l):
    if isNil(l):
        return True
    v = car(l)
    if isSymbol(v):
        return False
    if isList(v):
        if not isNil(v) and isSymbol(car(v)):
            return False
    return True

# Converts JSON properties to values
def jsPropertiesToValues(propertiesSoFar, o, i):
    if isNil(i):
        return propertiesSoFar
    p = car(i)
    jsv = o[p]
    v = jsValToValue(jsv)

    if isinstance(p, basestring):
        n = str(p)
        if n[0:1] == "@":
            return jsPropertiesToValues(cons((attribute, "'" + n[1:], v), propertiesSoFar), o, cdr(i))
        if isList(v) and not isJSArray(v):
            return jsPropertiesToValues(cons(cons(element, cons("'" + n, v)), propertiesSoFar), o, cdr(i))
        return jsPropertiesToValues(cons((element, "'" + n, v), propertiesSoFar), o, cdr(i))
    return jsPropertiesToValues(cons(v, propertiesSoFar), o, cdr(i))

# Converts a JSON val to a value
def jsValToValue(jsv):
    if isinstance(jsv, dict):
        return jsPropertiesToValues((), jsv, tuple(jsv.keys()))
    if isList(jsv):
        return jsPropertiesToValues((), jsv, tuple(reversed(range(0, len(jsv)))))
    if isinstance(jsv, basestring):
        return str(jsv)
    return jsv
    
# Convert a list of strings representing a JSON document to a list of values
def readJSON(l):
    s = StringIO()
    writeStrings(l, s)
    val = json.loads(s.getvalue())
    return jsValToValue(val)

# Convert a list of values to JSON array elements
def valuesToJSElements(a, l, i):
    if isNil(l):
        return a
    pv = valueToJSVal(car(l))
    a[i] = pv
    return valuesToJSElements(a, cdr(l), i + 1)
    
# Convert a value to a JSON value
def valueToJSVal(v):
    if not isList(v):
        return v
    if isJSArray(v):
        return valuesToJSElements(list(range(0, len(v))), v, 0)
    return valuesToJSProperties({}, v)

# Convert a list of values to JSON properties
def valuesToJSProperties(o, l):
    if isNil(l):
        return o
    token = car(l)
    if isTaggedList(token, attribute):
        pv = valueToJSVal(attributeValue(token))
        o["@" + attributeName(token)[1:]] = pv
    elif isTaggedList(token, element):
        if elementHasValue(token):
            pv = valueToJSVal(elementValue(token))
            o[elementName(token)[1:]] = pv
        else:
            child = {}
            o[elementName(token)[1:]] = child
            valuesToJSProperties(child, elementChildren(token))
    return valuesToJSProperties(o, cdr(l))

# Convert a list of values to a list of strings representing a JSON document
def writeJSON(l):
    jsv = valuesToJSProperties({}, l)
    s = json.dumps(jsv, separators=(',',':'))
    return (s,)

# Convert a list + params to a JSON-RPC request
def jsonRequest(id, func, params):
    r = (("'id", id), ("'method", func), ("'params", params))
    return writeJSON(valuesToElements(r))

# Convert a value to a JSON-RPC result
def jsonResult(id, val):
    return writeJSON(valuesToElements((("'id", id), ("'result", val))))

# Convert a JSON-RPC result to a value
def jsonResultValue(s):
    jsres = readJSON(s)
    res = elementsToValues(jsres)
    val = cadr(assoc("'result", res))
    if isList(val) and not isJSArray(val):
        return (val,)
    return val

# Return a portable function name from a JSON-RPC function name
def funcName(f):
    if f.startswith("."):
        return f[1:]
    if f.startswith("system."):
        return f[7:]
    if f.startswith("Service."):
        return f[8:]
    return f

