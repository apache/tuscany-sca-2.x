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
 * Functions to help represent data as lists of elements and attributes.
 */

var element = "'element"
var attribute = "'attribute"
var atsign = "'@"

/**
 * Return true if a value is an element.
 */
function isElement(v) {
    if (!isList(v) || isNil(v) || car(v) != element)
        return false;
    return true;
}

/**
 * Return true if a value is an attribute.
 */
function isAttribute(v) {
    if (!isList(v) || isNil(v) || car(v) != attribute)
        return false;
    return true;
}

/**
 * Return the name of an attribute.
 */
function attributeName(l) {
    return cadr(l);
}

/**
 * Return the value of an attribute.
 */
function attributeValue(l) {
    return caddr(l);
}

/**
 * Return the name of an element.
 */
function elementName(l) {
    return cadr(l);
}

/**
 * Return true if an element has children.
 */
function elementHasChildren(l) {
    return !isNil(cddr(l));
}

/**
 * Return the children of an element.
 */
function elementChildren(l) {
    return cddr(l);
}


/**
 * Return true if an element has a value.
 */
function elementHasValue(l) {
    r = reverse(l);
    if (isSymbol(car(r)))
        return false;
    if (isList(car(r)) && !isNil(car(r)) && isSymbol(car(car(r))))
        return false;
    return true;
}

/**
 * Return the value of an element.
 */
function elementValue(l) {
    return car(reverse(l));
}

/**
 * Convert an element to a value.
 */
function elementToValueIsList(v) {
    if (!isList(v))
        return false;
    return isNil(v) || !isSymbol(car(v));
}

function elementToValue(t) {
    if (isTaggedList(t, attribute))
        return mklist(atsign + attributeName(t).substring(1), attributeValue(t));
    if (isTaggedList(t, element)) {
        if (elementHasValue(t)) {
            if (!elementToValueIsList(elementValue(t)))
                return mklist(elementName(t), elementValue(t));
            return cons(elementName(t), mklist(elementsToValues(elementValue(t))));
        }
        return cons(elementName(t), elementsToValues(elementChildren(t)));
    }
    if (!isList(t))
        return t;
    return elementsToValues(t);
}

/**
 * Convert a list of elements to a list of values.
 */
function elementToValueIsSymbol(v) {
    if (!isList(v))
        return false;
    if (isNil(v))
        return false;
    if (!isSymbol(car(v)))
        return false;
    return true;
}

function elementToValueGroupValues(v, l) {
    if (isNil(l) || !elementToValueIsSymbol(v) || !elementToValueIsSymbol(car(l)))
        return cons(v, l);
    if (car(car(l)) != car(v))
        return cons(v, l);
    if (!elementToValueIsList(cadr(car(l)))) {
        var g = mklist(car(v), mklist(cdr(v), cdr(car(l))));
        return elementToValueGroupValues(g, cdr(l));
    }
    var g = mklist(car(v), cons(cdr(v), cadr(car(l))));
    return elementToValueGroupValues(g, cdr(l));
}

function elementsToValues(e) {
    if (isNil(e))
        return e;
    return elementToValueGroupValues(elementToValue(car(e)), elementsToValues(cdr(e)));
}

/**
 * Convert a value to an element.
 */
function valueToElement(t) {
    if (isList(t) && !isNil(t) && isSymbol(car(t))) {
        var n = car(t);
        var v = isNil(cdr(t))? mklist() : cadr(t);
        if (!isList(v)) {
            if (n.substring(0, 2) == atsign)
                return mklist(attribute, "'" + n.substring(2), v);
            return mklist(element, n, v);
        }
        if (isNil(v) || !isSymbol(car(v)))
            return cons(element, cons(n, mklist(valuesToElements(v))));
        return cons(element, cons(n, valuesToElements(cdr(t))));
    }
    if (!isList(t))
        return t;
    return valuesToElements(t);
}

/**
 * Convert a list of values to a list of elements.
 */
function valuesToElements(l) {
    if (isNil(l))
        return l;
    return cons(valueToElement(car(l)), valuesToElements(cdr(l)));
}

/**
 * Return a selector lambda function which can be used to filter elements.
 */
function selector(s) {
    function evalSelect(s, v) {
        if (isNil(s))
            return true;
        if (isNil(v))
            return false;
        if (car(s) != car(v))
            return false;
        return evalSelect(cdr(s), cdr(v));
    }

    return function(v) { return evalSelect(s, v); };
}

/**
 * Return the attribute with the given name.
 */
function namedAttribute(name, l) {
    return memo(l, name, function() {
        var f = filter(function(v) { return isAttribute(v) && attributeName(v) == name; }, l);
        if (isNil(f))
            return null;
        return car(f);
    });
}

/**
 * Return the value of the attribute with the given name.
 */
function namedAttributeValue(name, l) {
    var a = namedAttribute(name, l);
    if (a == null)
        return null
    return attributeValue(a);
}

/**
 * Return child elements with the given name.
 */
function namedElementChildren(name, l) {
    return memo(l, name, function() {
        return filter(function(v) { return isElement(v) && elementName(v) == name; }, l);
    });
}

/**
 * Return the child element with the given name.
 */
function namedElementChild(name, l) {
    var f = namedElementChildren(name, l);
    if (isNil(f))
        return null;
    return car(f);
}

/**
 * Side effect functions. Use with moderation.
 */

/**
 * Set the contents of an element.
 */
function setElement(l, e) {
    setlist(l, e);
    l.memo = {};
}

