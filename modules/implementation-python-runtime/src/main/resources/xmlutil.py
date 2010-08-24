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

# XML handling functions

from StringIO import StringIO
from xml.parsers import expat
import xml.etree.ElementTree as et
from util import *
from elemutil import *

# Read a list of XML attributes
def readAttributes(a):
    if a == ():
        return a
    return cons((attribute, "'" + car(car(a)), cadr(car(a))), readAttributes(cdr(a)))

# Read an XML element
def readElement(e):
    l = (element, "'" + e.tag) + readAttributes(tuple(e.items())) + readElements(tuple(e.getchildren()))
    if e.text == None:
        return l
    return l + (e.text,)

# Read a list of XML elements
def readElements(l):
    if l == ():
        return l
    return cons(readElement(car(l)), readElements(cdr(l)))

# Parse a list of strings representing an XML document
class NamespaceParser(et.XMLTreeBuilder):
    def __init__(self):
        et.XMLTreeBuilder.__init__(self)
        self._parser = parser = expat.ParserCreate(None)
        parser.DefaultHandlerExpand = self._default
        parser.StartElementHandler = self._start
        parser.EndElementHandler = self._end
        parser.CharacterDataHandler = self._data
        try:
            parser.buffer_text = 1
        except AttributeError:
            pass
        try:
            parser.ordered_attributes = 1
            parser.specified_attributes = 1
            parser.StartElementHandler = self._start_list
        except AttributeError:
            pass

def parseXML(l):
    s = StringIO()
    writeStrings(l, s)
    parser = NamespaceParser()
    parser.feed(s.getvalue())
    return parser.close()

# Read a list of values from a list of strings representing an XML document
def readXML(l):
    e = parseXML(l)
    return (readElement(e),)

# Write a list of XML element and attribute tokens
def expandElementValues(n, l):
    if isNil(l):
        return l
    return cons(cons(element, cons(n, car(l))), expandElementValues(n, cdr(l)))

def writeList(l, xml):
    if isNil(l):
        return xml
    token = car(l)
    if isTaggedList(token, attribute):
        xml.attrib[attributeName(token)[1:]] = str(attributeValue(token))
    elif isTaggedList(token, element):
        if elementHasValue(token):
            v = elementValue(token)
            if isList(v):
                e = expandElementValues(elementName(token), v)
                writeList(e, xml)
            else:
                child = et.Element(elementName(token)[1:])
                writeList(elementChildren(token), child)
                xml.append(child)
        else:
            child = et.Element(elementName(token)[1:])
            writeList(elementChildren(token), child)
            xml.append(child)
    else:
        xml.text = str(token)
    writeList(cdr(l), xml)
    return xml

# Convert a list of values to a list of strings representing an XML document
def writeXML(l, xmlTag):
    e = writeList(l, [])
    if not xmlTag:
        return (et.tostring(car(e)),)
    return (et.tostring(car(e), "UTF-8") + "\n",)

