#!/usr/bin/python
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

# Component invocation functions

from sys import stderr, argv
from util import *
from jsonutil import *

# JSON request id
id = 1

# Make a callable reference client
class proxy:
    def __init__(self, jpx):
        self.jpx = jpx

    def __call__(self, func, *args):

        # Create a JSON-RPC request
        global id
        req = StringIO()
        writeStrings(jsonRequest(id, func, args), req)
        id = id + 1

        # Eval the Java proxy
        res = self.jpx.eval(req.getvalue())

        # Extract result from JSON-RPC response
        return jsonResultValue((res,))

    def __repr__(self):
        return repr((jpx,))

def mkproxies(jpx):
    if isNil(jpx):
        return ()
    return cons(proxy(car(jpx)), mkproxies(cdr(jpx)))

class prop:
    def __init__(self, jpy):
        self.jpy = jpy

    def __call__(self):
        # Eval the property
        res = self.jpy.eval()
        return res

def __repr__(self):
    return repr((jpy,))

def mkprops(jpy):
    if isNil(jpy):
        return ()
    return cons(prop(car(jpy)), mkprops(cdr(jpy)))

# Make a callable component
class component:
    def __init__(self, name, impl, jpx, jpy):
        self.name = name
        self.impl = impl[0:len(impl) - 3]
        self.mod = __import__(self.impl)
        self.proxies = mkproxies(jpx)
        self.props = mkprops(jpy)

    def __call__(self, func, *args):
        return self.mod.__getattribute__(func)(*(args + self.proxies + self.props))

    def __repr__(self):
        return repr((self.name, self.impl, self.mod, self.props, self.proxies))

# Converts the args received in a JSON request to a list of key value pairs
def jsonArgs(a):
    if isNil(a):
        return ((),)
    l = car(a);
    return cons(l, jsonArgs(cdr(a)))

# Apply a JSON function request to a component
def apply(jsreq, comp):
    json = elementsToValues(readJSON((jsreq,)))
    args = jsonArgs(json)
    jid = cadr(assoc("'id", args))
    func = funcName(cadr(assoc("'method", args)))
    params = cadr(assoc("'params", args))
    v = comp(func, *params)
    return jsonResult(jid, v)[0]

# Make a component that can be called with a JSON function request
def mkcomponent(name, impl, jpx, jpy):
    comp = component(name, impl, jpx, jpy)
    return lambda jsreq: apply(jsreq, comp)

