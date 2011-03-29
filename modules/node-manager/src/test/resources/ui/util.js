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
 * Simple utility functions.
 */

/**
 * Scheme-like lists.
 */
function cons(car, cdr) {
    var a = new Array();
    a.push(car);
    return a.concat(cdr);
}

function car(l) {
    return l[0];
}

function first(l) {
    return car(l);
}

function cdr(l) {
    return l.slice(1);
}

function rest(l) {
    return cdr(l);
}

function cadr(l) {
    return car(cdr(l));
}

function cddr(l) {
    return cdr(cdr(l));
}

function caddr(l) {
    return car(cddr(l));
}

function cdddr(l) {
    return cdr(cdr(cdr(l)));
}

function cadddr(l) {
    return car(cdddr(l));
}

function append(a, b) {
    return a.concat(b);
}

function reverse(l) {
    return l.slice(0).reverse();
}

function range(a, b) {
    var l = new Array();
    for (var x = a; x < b; x++)
        l.push(x);
    return l;
}

function isNil(v) {
    if (v == null || typeof v == 'undefined' || (v.constructor == Array && v.length == 0))
        return true;
    return false;
}

function isSymbol(v) {
    if (typeof v == 'string' && v.slice(0, 1) == "'")
        return true;
    return false;
}

function isString(v) {
    if (typeof v == 'string' && v.slice(0, 1) != "'")
        return true;
    return false;
}

function isList(v) {
    if (v != null && typeof v != 'undefined' && v.constructor == Array)
        return true;
    return false;
}

function isTaggedList(v, t) {
    if (isList(v) && !isNil(v) && car(v) == t)
        return true;
    return false;
}

var emptylist = new Array();

function mklist() {
    if (arguments.length == 0)
        return emptylist;
    var a = new Array();
    for (i = 0; i < arguments.length; i++)
        a[i] = arguments[i];
    return a;
}

function length(l) {
    return l.length;
}

/**
 * Scheme-like associations.
 */
function assoc(k, l) {
    if (isNil(l))
        return mklist();
    if (k == car(car(l)))
        return car(l);
    return assoc(k, cdr(l));
}

/**
 * Map, filter and reduce functions.
 */
function map(f, l) {
    if (isNil(l))
        return l;
    return cons(f(car(l)), map(f, cdr(l)));
}

function filter(f, l) {
    if (isNil(l))
        return l;
    if (f(car(l)))
        return cons(car(l), filter(f, cdr(l)));
    return filter(f, cdr(l));
}

function reduce(f, i, l) {
    if (isNil(l))
        return i;
    return reduce(f, f(i, car(l)), cdr(l));
}

/**
 * Split a path into a list of segments.
 */
function tokens(path) {
    return filter(function(s) { return length(s) != 0; }, path.split("/"));
}

/**
 * Log a value.
 */
var rconsole;

function log(v) {
    try {
        var s = '';
        for (i = 0; i < arguments.length; i++) {
            s = s + writeValue(arguments[i]);
            if (i < arguments.length)
                s = s + ' ';
        }

        if (rconsole) {
            try {
                rconsole.log(s);
            } catch (e) {}
        }
        try {
            console.log(s);
        } catch (e) {}
    } catch (e) {}
    return true;
}

/**
 * Dump an object to the debug console.
 */
function debug(o) {
    try {
        for (f in o) {
            try {
                log('debug ' + f + '=' + o[f]);
            } catch (e) {}
        }
    } catch (e) {}
    return true;
}

/**
 * Simple assert function.
 */
function AssertException() {
}

AssertException.prototype.toString = function () {
    return 'AssertException';
};

function assert(exp) {
    if (!exp)
        throw new AssertException();
}

/**
 * Write a list of strings.
 */
function writeStrings(l) {
    if (isNil(l))
        return '';
    return car(l) + writeStrings(cdr(l));
}

/**
 * Write a value using a Scheme-like syntax.
 */
function writeValue(v) {
    function writePrimitive(p) {
        if (isSymbol(p))
            return '' + p.substring(1);
        if (isString(p))
            return '"' + p + '"';
        return '' + p;
    }

    function writeList(l) {
        if (isNil(l))
            return '';
        return ' ' + writeValue(car(l)) + writeList(cdr(l));
    }

    if (!isList(v))
        return writePrimitive(v);
    if (isNil(v))
        return '()';
    return '(' + writeValue(car(v)) + writeList(cdr(v)) + ')';
}

/**
 * Apply a function and memoize its result.
 */
function memo(obj, key, f) {
    if (!obj[memo])
        obj.memo = {};
    if (obj.memo[key])
        return obj.memo[key];
    return obj.memo[key] = f();
}

/**
 * Un-memoize store results.
 */
function unmemo(obj) {
    obj.memo = {};
    return true;
}

/**
 * Returns a list of the properties of an object.
 */
function properties(o) {
    var a = new Array();
    for (p in o)
        a.push(p);
    return a;
}

/**
 * Functions with side effects. Use with moderation.
 */

/**
 * Set the car of a list.
 */
function setcar(l, v) {
    l[0] = v;
    return l;
}

/**
 * Set the cadr of a list.
 */
function setcadr(l, v) {
    l[1] = v;
    return l;
}

/**
 * Set the caddr of a list.
 */
function setcaddr(l, v) {
    l[2] = v;
    return l;
}

/**
 * Append the elements of a list to a list.
 */
function setappend(a, b) {
    if (isNil(b))
        return a;
    a.push(car(b));
    return setappend(a, cdr(b));
}

/**
 * Set the cdr of a list.
 */
function setcdr(a, b) {
    a.length = 1;
    return setappend(a, b);
}

/**
 * Set the contents of a list.
 */
function setlist(a, b) {
    if (b == a)
        return b;
    a.length = 0;
    return setappend(a, b);
}

