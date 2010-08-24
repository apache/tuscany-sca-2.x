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

# Functions to help represent data as lists of elements and attributes

from util import *

element = "'element"
attribute = "'attribute"
atsign = "'@"

# Return true if a value is an element
def isElement(v):
    if not isList(v) or isNil(v) or v == None or car(v) != element:
        return False
    return True

# Return true if a value is an attribute
def isAttribute(v):
    if not isList(v) or isNil(v) or v == None or car(v) != attribute:
        return False
    return True

# Return the name of attribute
def attributeName(l):
    return cadr(l)

# Return the value of attribute
def attributeValue(l):
    return caddr(l)

# Return the name of an element
def elementName(l):
    return cadr(l)

# Return true if an element has children
def elementHasChildren(l):
    return not isNil(cddr(l))

# Return the children of an element
def elementChildren(l):
    return cddr(l)

# Return true if an element has a value
def elementHasValue(l):
    r = reverse(l)
    if isSymbol(car(r)):
        return False
    if isList(car(r)) and not isNil(car(r)) and isSymbol(car(car(r))):
        return False
    return True

# Return the value of an element
def elementValue(l):
    return car(reverse(l))

# Convert an element to a value
def elementToValueIsList(v):
    if not isList(v):
        return False
    return isNil(v) or not isSymbol(car(v))

def elementToValue(t):
    if isTaggedList(t, attribute):
        return (atsign + attributeName(t)[1:], attributeValue(t))
    if isTaggedList(t, element):
        if elementHasValue(t):
            if not elementToValueIsList(elementValue(t)):
                return (elementName(t), elementValue(t))
            return cons(elementName(t), (elementsToValues(elementValue(t)),))
        return cons(elementName(t), elementsToValues(elementChildren(t)))
    if not isList(t):
        return t
    return elementsToValues(t)

# Convert a list of elements to a list of values
def elementToValueIsSymbol(v):
    if not isList(v):
        return False
    if (isNil(v)):
        return False
    if not isSymbol(car(v)):
        return False
    return True

def elementToValueGroupValues(v, l):
    if isNil(l) or not elementToValueIsSymbol(v) or not elementToValueIsSymbol(car(l)):
        return cons(v, l)
    if car(car(l)) != car(v):
        return cons(v, l)
    if not elementToValueIsList(cadr(car(l))):
        g = (car(v), (cdr(v), cdr(car(l))))
        return elementToValueGroupValues(g, cdr(l))
    g = (car(v), cons(cdr(v), cadr(car(l))))
    return elementToValueGroupValues(g, cdr(l))

def elementsToValues(e):
    if isNil(e):
        return e
    return elementToValueGroupValues(elementToValue(car(e)), elementsToValues(cdr(e)))

# Convert a value to an element
def valueToElement(t):
    if isList(t) and not isNil(t) and isSymbol(car(t)):
        n = car(t)
        v = cadr(t)
        if not isList(v):
            if n[0:2] == atsign:
                return (attribute, n[1:], v)
            return (element, n, v)
        if isNil(v) or not isSymbol(car(v)):
            return cons(element, cons(n, (valuesToElements(v),)))
        return cons(element, cons(n, valuesToElements(cdr(t))))
    if not isList(t):
        return t
    return valuesToElements(t)

# Convert a list of values to a list of elements
def valuesToElements(l):
    if isNil(l):
        return l
    return cons(valueToElement(car(l)), valuesToElements(cdr(l)))

# Return a selector lambda function which can be used to filter elements
def evalSelect(s, v):
    if isNil(s):
        return True
    if isNil(v):
        return False
    if car(s) != car(v):
        return False
    return evalSelect(cdr(s), cdr(v))

def selector(s):
    return lambda v: evalSelect(s, v)

# Return the value of the attribute with the given name
def namedAttributeValue(name, l):
    f = filter(lambda v: isAttribute(v) and attributeName(v) == name, l)
    if isNil(f):
        return None
    return caddr(car(f))

# Return child elements with the given name
def namedElementChildren(name, l):
    return filter(lambda v: isElement(v) and elementName(v) == name, l)

# Return the child element with the given name
def namedElementChild(name, l):
    f = namedElementChildren(name, l)
    if isNil(f):
        return None
    return car(f)

