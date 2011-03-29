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
 * XML handling functions.
 */

/**
 * Convert a DOM node list to a regular list.
 */
function nodeList(n) {
    var l = new Array();
    if (isNil(n))
        return l;
    for (var i = 0; i < n.length; i++)
        l[i] = n[i];
    return l;
}

/**
 * Append a list of nodes to a parent node.
 */
function appendNodes(nodes, p) {
    if (isNil(nodes))
        return p;
    p.appendChild(car(nodes));
    return appendNodes(cdr(nodes), p);
};

/**
 * Return the child attributes of an element.
 */
function childAttributes(e) {
    return filter(function(n) { return n.nodeType == 2; }, nodeList(e.attributes));
}

/**
 * Return the child elements of an element.
 */
function childElements(e) {
    return filter(function(n) { return n.nodeType == 1; }, nodeList(e.childNodes));
}

/**
 * Return the child text nodes of an element.
 */
function childText(e) {
    function trim(s) {
        return s.replace(/^\s*/, '').replace(/\s*$/, '');
    }
    return filter(function(n) { return n.nodeType == 3 && trim(n.nodeValue) != ''; }, nodeList(e.childNodes));
}

/**
 * Read a list of XML attributes.
 */
function readAttributes(p, a) {
    if (isNil(a))
        return a;
    var x = car(a);
    return cons(mklist(attribute, "'" + x.nodeName, x.nodeValue), readAttributes(p, cdr(a)));
}

/**
 * Read an XML element.
 */
function readElement(e, childf) {
    var l = append(append(mklist(element, "'" + e.nodeName), readAttributes(e, childf(e))), readElements(childElements(e), childf));
    var t = childText(e);
    if (isNil(t))
        return l;
    return append(l, mklist(car(t).nodeValue));
}

/**
 * Read a list of XML elements.
 */
function readElements(l, childf) {
    if (isNil(l))
        return l;
    return cons(readElement(car(l), childf), readElements(cdr(l), childf));
}

/**
 * Return true if a list of strings contains an XML document.
 */
function isXML(l) {
    if (isNil(l))
        return false;
    return car(l).substring(0, 5) == '<?xml';
}

/**
 * Parse a list of strings representing an XML document.
 */
function parseXML(l) {
    var s = writeStrings(l);
    if (window.DOMParser) {
        var p = new DOMParser();
        return p.parseFromString(s, "text/xml");
    }
    var doc;
    try {
        doc = new ActiveXObject("MSXML2.DOMDocument");
    } catch (e) {
        doc = new ActiveXObject("Microsoft.XMLDOM");
    }
    doc.async = 'false';
    doc.loadXML(s); 
    return doc;
}

/**
 * Read a list of values from an XML document.
 */
function readXMLDocument(doc) {
    var root = childElements(doc);
    if (isNil(root))
        return mklist();
    return mklist(readElement(car(root), childAttributes));
}

/**
 * Read a list of values from an XHTML element.
 */
function readXHTMLElement(xhtml) {
    // Special XHTML attribute filtering on IE
    function ieChildAttributes(e) {
        var a = filter(function(n) {
            // Filter out empty and internal DOM attributes
            if (n.nodeType != 2 || isNil(n.nodeValue) || n.nodeValue == '')
                return false;
            if (n.nodeName == 'contentEditable' || n.nodeName == 'maxLength' || n.nodeName == 'loop' || n.nodeName == 'start')
                return false;
            return true;
        }, nodeList(e.attributes));

        if (e.style.cssText == '')
            return a;

        // Add style attribute
        var sa = new Object();
        sa.nodeName = 'style';
        sa.nodeValue = e.style.cssText;
        return cons(sa, a);
    }

    var childf = (typeof(XMLSerializer) != 'undefined')? childAttributes : ieChildAttributes;
    return mklist(readElement(xhtml, childf));
}

/**
 * Read a list of values from a list of strings representing an XML document.
 */
function readXML(l) {
    return readXMLDocument(parseXML(l));
}

/**
 * Return a list of strings representing an XML document.
 */
function writeXMLDocument(doc) {
    if (typeof(XMLSerializer) != 'undefined')
        return mklist(new XMLSerializer().serializeToString(doc));
    return mklist(doc.xml);
}

/**
 * Write a list of XML element and attribute tokens.
 */
function expandElementValues(n, l) {
    if (isNil(l))
        return l;
    return cons(cons(element, cons(n, car(l))), expandElementValues(n, cdr(l)));
}

function writeList(l, node, doc) {
    if (isNil(l))
        return node;

    var token = car(l);
    if (isTaggedList(token, attribute)) {
        node.setAttribute(attributeName(token).substring(1), '' + attributeValue(token));

    } else if (isTaggedList(token, element)) {

        function mkelem(tok, doc) {
            function xmlns(l) {
                if (isNil(l))
                    return null;
                var t = car(l);
                if (isTaggedList(t, attribute)) {
                    if (attributeName(t).substring(1) == 'xmlns')
                        return attributeValue(t);
                }
                return xmlns(cdr(l));
            }

            var ns = xmlns(elementChildren(tok));
            if (ns == null || !doc.createElementNS)
                return doc.createElement(elementName(tok).substring(1));
            return doc.createElementNS(ns, elementName(tok).substring(1));
        }

        if (elementHasValue(token)) {
            var v = elementValue(token);
            if (isList(v)) {
                var e = expandElementValues(elementName(token), v);
                writeList(e, node, doc);
            } else {
                var child = mkelem(token, doc);
                writeList(elementChildren(token), child, doc);
                node.appendChild(child);
            }
        } else {
            var child = mkelem(token, doc);
            writeList(elementChildren(token), child, doc);
            node.appendChild(child);
        }
    } else
        node.appendChild(doc.createTextNode('' + token));

    writeList(cdr(l), node, doc);
    return node;
}

/**
 * Make a new XML document.
 */
function mkXMLDocument() { 
    if (document.implementation && document.implementation.createDocument)
        return document.implementation.createDocument('', '', null); 
    return new ActiveXObject("MSXML2.DOMDocument"); 
}

/**
 * Convert a list of values to a list of strings representing an XML document.
 */
function writeXML(l, xmlTag) {
    var doc = mkXMLDocument();
    writeList(l, doc, doc);
    if (!xmlTag)
        return writeXMLDocument(doc);
    return mklist('<?xml version="1.0" encoding="UTF-8"?>\n' + writeXMLDocument(doc) + '\n');
}

