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

# RSS data conversion functions

from util import *
from elemutil import *
from xmlutil import *

# Convert a list of elements to a list of values representing an RSS entry
def entryElementsToValues(e):
    lt = filter(selector((element, "'title")), e)
    t = "" if isNil(lt) else elementValue(car(lt))
    li = filter(selector((element, "'link")), e)
    i = "" if isNil(li) else elementValue(car(li))
    lc = filter(selector((element, "'description")), e)
    return (t, i, elementValue(car(lc)))

# Convert a list of elements to a list of values representing RSS entries
def entriesElementsToValues(e):
    if isNil(e):
        return e
    return cons(entryElementsToValues(car(e)), entriesElementsToValues(cdr(e)))

# Convert a list of strings to a list of values representing an RSS entry
def readRSSEntry(l):
    e = readXML(l)
    if isNil(e):
        return ()
    return entryElementsToValues(car(e))

# Convert a list of values representy an RSS entry to a value
def entryValue(e):
    v = elementsToValues((caddr(e),))
    return cons(car(e), (cadr(e), cdr(car(v))))

# Return true if a list of strings represents an RSS feed
def isRSSFeed(l):
    if isNil(l):
        return False
    if car(l)[0:5] != "<?xml":
        return False
    return contains(car(l), "<rss")

# Convert a list of strings to a list of values representing an RSS feed
def readRSSFeed(l):
    f = readXML(l)
    if isNil(f):
        return ()
    c = filter(selector((element, "'channel")), car(f))
    t = filter(selector((element, "'title")), car(c))
    i = filter(selector((element, "'link")), car(c))
    e = filter(selector((element, "'item")), car(c))
    if isNil(e):
        return (elementValue(car(t)), elementValue(car(i)))
    return cons(elementValue(car(t)), cons(elementValue(car(i)), entriesElementsToValues(e)))

# Convert an RSS feed containing elements to an RSS feed containing values
def feedValuesLoop(e):
    if (isNil(e)):
        return e
    return cons(entryValue(car(e)), feedValuesLoop(cdr(e)))

def feedValues(e):
    return cons(car(e), cons(cadr(e), feedValuesLoop(cddr(e))))

# Convert a list of values representy an RSS entry to a list of elements
def entryElement(l):
    return (element, "'item",
            (element, "'title", car(l)),
            (element, "'link", cadr(l)),
            (element, "'description", caddr(l)))

# Convert a list of values representing RSS entries to a list of elements
def entriesElements(l):
    if isNil(l):
        return l
    return cons(entryElement(car(l)), entriesElements(cdr(l)))

# Convert a list of values representing an RSS entry to an RSS entry
def writeRSSEntry(l):
    return writeXML((entryElement(l),), True)

# Convert a list of values representing an RSS feed to an RSS feed
def writeRSSFeed(l):
    c = ((element, "'title", car(l)),
            (element, "'link", cadr(l)),
            (element, "'description", car(l)))
    ce = c if isNil(cddr(l)) else append(c, entriesElements(cddr(l)))
    fe = (element, "'rss", (attribute, "'version", "2.0"), append((element, "'channel"), ce))
    return writeXML((fe,), True)

# Convert an RSS entry containing a value to an RSS entry containing an item element
def entryValuesToElements(v):
    return cons(car(v), cons(cadr(v), valuesToElements((cons("'item", caddr(v)),))))

# Convert an RSS feed containing values to an RSS feed containing elements
def feedValuesToElementsLoop(v):
    if isNil(v):
        return v
    return cons(entryValuesToElements(car(v)), feedValuesToElementsLoop(cdr(v)))

def feedValuesToElements(v):
    return cons(car(v), cons(cadr(v), feedValuesToElementsLoop(cddr(v))))

