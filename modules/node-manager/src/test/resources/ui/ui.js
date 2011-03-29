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
 * UI utility functions.
 */

var ui = {};

/**
 * Return true if the current browser is Internet Explorer.
 */
ui.isIE = function() {
    if (typeof ui.isIE.detected != 'undefined')
        return ui.isIE.detected;
    ui.isIE.detected = navigator.appName == 'Microsoft Internet Explorer';
    return ui.isIE.detected;
};

/**
 * Build a menu bar.
 */ 
ui.menu = function(name, href) {
    function Menu(n, h) {
        this.name = n;
        this.href = h;

        this.content = function() {
            function complete(uri) {
                var q = uri.indexOf('?');
                if (q != -1)
                    return complete(uri.substr(0, q));
                if (uri.match('.*\.html$'))
                    return uri;
                if (uri.match('.*/$'))
                    return uri + 'index.html';
                return uri + '/index.html';
            }

            if (complete(this.href) != complete(window.top.location.pathname))
                return '<a href="' + this.href + '" target="_parent"><span class=amenu>' + this.name + '</span></a>';
            return '<a href="' + this.href + '" target="_parent"><span class=smenu>' + this.name + '</span></a>';
        };
    }
    return new Menu(name, href);
};

ui.menubar = function(left, right) {
    var bar = '<table cellpadding="0" cellspacing="0" width="100%" class=tbar><tr>' +
    '<td class=ltbar><table border="0" cellspacing="0" cellpadding="0"><tr>';
    for (i in left)
        bar = bar + '<td class=ltbar>' + left[i].content() + '</td>'

    bar = bar + '</tr></table></td>' +
    '<td class=rtbar><table border="0" cellpadding="0" cellspacing="0" align="right"><tr>';
    for (i in right)
        bar = bar + '<td class=rtbar>' + right[i].content() + '</td>'

    bar = bar + '</tr></table></td></tr></table>';
    return bar;
};
 
/**
 * Autocomplete / suggest support for input fields
 * To use it declare a 'suggest' function as follows:
 * function suggestItems() {
 *       return new Array('abc', 'def', 'ghi');
 * }
 * then hook it to an input field as follows:
 * suggest(document.yourForm.yourInputField, suggestItems);
 */ 
ui.selectSuggestion = function(node, value) {
    for (;;) {
        node = node.parentNode;
        if (node.tagName.toLowerCase() == 'div')
              break;
    }
    node.selectSuggestion(value);
};

ui.hilightSuggestion = function(node, over) {
    if (over)
        node.className = 'suggestHilighted';
    node.className = 'suggestItem';
};

ui.suggest = function(input, suggestFunction) {
    input.suggest = suggestFunction;
      
    input.selectSuggestion = function(value) {
        this.hideSuggestDiv();
        this.value = value;
    }
    
    input.hideSuggestDiv = function() {
        if (this.suggestDiv != null) {
            this.suggestDiv.style.visibility = 'hidden';
        }
    }
    
    input.showSuggestDiv = function() {
        if (this.suggestDiv == null) {
            this.suggestDiv = document.createElement('div');
            this.suggestDiv.input = this;
            this.suggestDiv.className = 'suggest';
            input.parentNode.insertBefore(this.suggestDiv, input);
            this.suggestDiv.style.visibility = 'hidden';
            this.suggestDiv.style.zIndex = '99';
            
            this.suggestDiv.selectSuggestion = function(value) {
                this.input.selectSuggestion(value);
            }
        }
        
        var values = this.suggest();
        var items = '';
        for (var i = 0; i < values.length; i++) {
            if (values[i].indexOf(this.value) == -1)
                continue;
            if (items.length == 0)
                items += '<table class=suggestTable>';
            items += '<tr><td class="suggestItem" ' +
            'onmouseover="ui.hilightSuggestion(this, true)" onmouseout="ui.hilightSuggestion(this, false)" ' +
            'onmousedown="ui.selectSuggestion(this, \'' + values[i] + '\')">' + values[i] + '</td></tr>';
        }
        if (items.length != 0)
            items += '</table>';
        this.suggestDiv.innerHTML = items;
        
        if (items.length != 0) {
            var node = input;              
            var left = 0;
            var top = 0;
            for (;;) {
                left += node.offsetLeft;
                top += node.offsetTop;
                node = node.offsetParent;
                if (node.tagName.toLowerCase() == 'body')
                  break;
            }
            this.suggestDiv.style.left = left;
            this.suggestDiv.style.top = top + input.offsetHeight;
            this.suggestDiv.style.visibility = 'visible';
        } else
            this.suggestDiv.style.visibility = 'hidden';
    }
      
    input.onkeydown = function(event) {
        this.showSuggestDiv();
    };

    input.onkeyup = function(event) {
        this.showSuggestDiv();
    };

    input.onmousedown = function(event) {
        this.showSuggestDiv();
    };

    input.onblur = function(event) {
        setTimeout(function() { input.hideSuggestDiv(); }, 50);
    };
};

/**
 * Return the content document of a window.
 */
ui.content = function(win) {
    if (!isNil(win.document))
        return win.document;
    if (!isNil(win.contentDocument))
        return win.contentDocument;
    return null;
};

/**
 * Return a child element of a node with the given id.
 */
ui.elementByID = function(node, id) {
    for (var i in node.childNodes) {
        var child = node.childNodes[i];
        if (child.id == id)
            return child;
        var gchild = ui.elementByID(child, id);
        if (gchild != null)
            return gchild;
    }
    return null;
};

/**
 * Return the current document, or a child element with the given id.
 */
function $(id) {
    if (id == document) {
        if (!isNil(document.widget))
            return document.widget;
        return document;
    }
    return ui.elementByID($(document), id);
};

/**
 * Return a dictionary of the query parameters.
 */
ui.queryParams = function() {
    var qp = new Array();
    var qs = window.location.search.substring(1).split('&');
    for (var i = 0; i < qs.length; i++) {
        var e = qs[i].indexOf('=');
        if (e > 0)
            qp[qs[i].substring(0, e)] = unescape(qs[i].substring(e + 1));
    }
    return qp;
}

/**
 * Bind a widget iframe to an element.
 */
ui.widgets = {};
ui.onload = {};

ui.loadwidget = function(el, doc, cb) {
    var f = el + 'Frame';
    window.ui.widgets[f] = el;
    window.ui.onload[f] = cb;
    var div = document.createElement('div');
    div.id = f + 'Div';
    div.innerHTML = '<iframe id="' + f + '" class="widgetframe" scrolling="no" frameborder="0" src="' + doc + '" onload="window.ui.onload[this.id]()"></iframe>';
    document.body.appendChild(div);
    return f;
};

/**
 * Show the current document body.
 */
ui.showbody = function() {
    document.body.style.visibility = 'visible';
};

/**
 * Install a widget into the element bound to its iframe.
 */
ui.installwidget = function() {
    if (isNil(window.parent) || isNil(window.parent.ui) || isNil(window.parent.ui.widgets))
        return true;
    var pdoc = ui.content(window.parent);
    for (w in window.parent.ui.widgets) {
        var ww = ui.elementByID(pdoc, w).contentWindow;
        if (ww == window) {
            document.widget = ui.elementByID(pdoc, window.parent.ui.widgets[w]);
            document.widget.innerHTML = document.body.innerHTML;
            return true;
        }
    }
    return true;
};

/**
 * Load an iframe into an element.
 */
ui.loadiframe = function(el, doc) {
    var f = el + 'Frame';
    $(el).innerHTML =
        '<iframe id="' + f + '" class="loadedframe" scrolling="no" frameborder="0" src="' + doc + '"></iframe>';
    return f;
};

/**
 * Convert a CSS position to a numeric position.
 */
ui.csspos = function(p) {
    if (p == '')
        return 0;
    return Number(p.substr(0, p.length - 2));
};

/**
 * Convert a list of elements to an HTML table.
 */
ui.datatable = function(l) {
    log('datatable', writeValue(l));

    function indent(i) {
        if (i == 0)
            return '';
        return '&nbsp;&nbsp;' + indent(i - 1);
    }

    function rows(l, i) {
        if (isNil(l))
            return '';
        var e = car(l);

        if (!isList(e))
            return rows(expandElementValues("'value", l), i);

        if (elementHasValue(e)) {
            var v = elementValue(e);
            if (!isList(v)) {
                return '<tr><td class="datatdl">' + indent(i) + elementName(e).slice(1) + '</td>' +
                    '<td class="datatdr">' + v + '</td></tr>' +
                    rows(cdr(l), i);
            }

            return rows(expandElementValues(elementName(e), v), i) + rows(cdr(l), i);
        }

        return '<tr><td class="datatdl">' + indent(i) + elementName(e).slice(1) + '</td>' +
            '<td class="datatdr">' + '</td></tr>' +
            rows(elementChildren(e), i + 1) +
            rows(cdr(l), i);
    }

    return '<table class="datatable ' + (window.name == 'dataFrame'? ' databg' : '') + '" style="width: 100%;">' + rows(l, 0) + '</table>';
}

