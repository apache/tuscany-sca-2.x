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

# ATOM data conversion functions

from util import *
from elemutil import *
from xmlutil import *

# Convert a list of elements to a list of values representing an ATOM entry
def entryElementsToValues(e):
    lt = filter(selector((element, "'title")), e)
    t = "" if isNil(lt) else elementValue(car(lt))
    li = filter(selector((element, "'id")), e)
    i = "" if isNil(li) else elementValue(car(li))
    lc = filter(selector((element, "'content")), e)
    return (t, i, elementValue(car(lc)))

# Convert a list of elements to a list of values representing ATOM entries
def entriesElementsToValues(e):
    if isNil(e):
        return e
    return cons(entryElementsToValues(car(e)), entriesElementsToValues(cdr(e)))

# Convert a list of strings to a list of values representing an ATOM entry
def readATOMEntry(l):
    e = readXML(l)
    if isNil(e):
        return ()
    return entryElementsToValues(car(e))

# Convert a list of values representy an ATOM entry to a value
def entryValue(e):
    v = elementsToValues((caddr(e),))
    return cons(car(e), (cadr(e), cdr(car(v))))

# Return true if a list of strings represents an ATOM feed
def isATOMFeed(l):
    if isNil(l):
        return False
    if car(l)[0:5] != "<?xml":
        return False
    return contains(car(l), "<feed")

# Convert a list of strings to a list of values representing an ATOM feed
def readATOMFeed(l):
    f = readXML(l)
    if isNil(f):
        return ()
    t = filter(selector((element, "'title")), car(f))
    i = filter(selector((element, "'id")), car(f))
    e = filter(selector((element, "'entry")), car(f))
    if isNil(e):
        return (elementValue(car(t)), elementValue(car(i)))
    return cons(elementValue(car(t)), cons(elementValue(car(i)), entriesElementsToValues(e)))

# Convert an ATOM feed containing elements to an ATOM feed containing values
def feedValuesLoop(e):
    if (isNil(e)):
        return e
    return cons(entryValue(car(e)), feedValuesLoop(cdr(e)))

def feedValues(e):
    return cons(car(e), cons(cadr(e), feedValuesLoop(cddr(e))))

# Convert a list of values representy an ATOM entry to a list of elements
def entryElement(l):
    return (element, "'entry", (attribute, "'xmlns", "http://www.w3.org/2005/Atom"),
            (element, "'title", (attribute, "'type", "text"), car(l)),
            (element, "'id", cadr(l)),
            (element, "'content", (attribute, "'type", ("application/xml" if isList(caddr(l)) else "text")), caddr(l)),
            (element, "'link", (attribute, "'href", cadr(l))))

# Convert a list of values representing ATOM entries to a list of elements
def entriesElements(l):
    if isNil(l):
        return l
    return cons(entryElement(car(l)), entriesElements(cdr(l)))

# Convert a list of values representing an ATOM entry to an ATOM entry
def writeATOMEntry(l):
    return writeXML((entryElement(l),), True)

# Convert a list of values representing an ATOM feed to an ATOM feed
def writeATOMFeed(l):
    f = (element, "'feed", (attribute, "'xmlns", "http://www.w3.org/2005/Atom"),
            (element, "'title", (attribute, "'type", "text"), car(l)),
            (element, "'id", cadr(l)))
    if isNil(cddr(l)):
        return writeXML((f,), True)
    fe = append(f, entriesElements(cddr(l)))
    return writeXML((fe,), True)

# Convert an ATOM entry containing a value to an ATOM entry containing an item element
def entryValuesToElements(v):
    return cons(car(v), cons(cadr(v), valuesToElements((cons("'item", caddr(v)),))))

# Convert an ATOM feed containing values to an ATOM feed containing elements
def feedValuesToElementsLoop(v):
    if isNil(v):
        return v
    return cons(entryValuesToElements(car(v)), feedValuesToElementsLoop(cdr(v)))

def feedValuesToElements(v):
    return cons(car(v), cons(cadr(v), feedValuesToElementsLoop(cddr(v))))

