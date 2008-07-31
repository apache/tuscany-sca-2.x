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
 * Autocomplete / suggest support for input fields
 *
 * To use it declare a 'suggest' function as follows:
 * function suggestItems() {
 *   	return new Array('abc', 'def', 'ghi');
 * }
 *
 * then hook it to an input field as follows:
 * suggest(document.yourForm.yourInputField, suggestItems);
 */ 
function selectSuggestion(node, value) {
	for (;;) {
		node = node.parentNode;
		if (node.tagName.toLowerCase() == 'div') {
      		break;
      	}
	}
	node.selectSuggestion(value);
}

function hilightSuggestion(node, over) {
	if (over) {
  		node.className = 'suggestHilighted';
	} else {
		node.className = 'suggestItem';
	}
}

function suggest(input, suggestFunction) {
  	
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
    	var items = "";
    	for (var i = 0; i < values.length; i++) {
    		if (values[i].indexOf(this.value) == -1) {
    			continue;
    		}
    		if (items.length == 0) {
    			items += '<table class=suggestTable>';
    		}
    		items += '<tr><td class="suggestItem" ' +
    		'onmouseover="hilightSuggestion(this, true)" onmouseout="hilightSuggestion(this, false)" ' +
    		'onmousedown="selectSuggestion(this, \'' + values[i] + '\')">' + values[i] + '</td></tr>';
    	}
    	if (items.length != 0) {
    		items += '</table>';
    	}
	  	this.suggestDiv.innerHTML = items;
	  	
		if (items.length != 0) {
			var node = input;		  	
	        var left = 0;
	        var top = 0;
	        for (;;) {
	            left += node.offsetLeft;
	            top += node.offsetTop;
	            node = node.offsetParent;
	            if (node.tagName.toLowerCase() == 'body') {
	            	break;
	            }
	        }
		  	this.suggestDiv.style.left = left;
		  	this.suggestDiv.style.top = top + input.offsetHeight;
		  	this.suggestDiv.style.visibility = 'visible';
		} else {
			this.suggestDiv.style.visibility = 'hidden';
		}
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
}

/**
 * A Toolbar class
 */ 
function Tool(name, href) {
	this.name = name;
	this.href = href;
}

Tool.prototype.print = function() {
    var loc = '' + location;
	if (loc.match(this.href) == null) {
		return '<a href="' + this.href + '" target="_parent">' + this.name + '</a>';
	} else {
		return '<span>' + this.name + '</span>';
	}
}

/**
 * Initialize the toolbar
 */
function toolbar(home, tools) {
	var toolbar = '<table width="100%" cellpadding="0" cellspacing="0" class=tbar><tr>' +
	'<td class=ltbar><table border="0" cellspacing="0" cellpadding="0"><tr>';
   
	for (var i = 0; i < tools.length; i++) {
	   toolbar = toolbar + '<td class=ltbar>' +tools[i].print() + '</td>'
   	}
   
   	toolbar = toolbar + '</tr></table></td>' + 
   	'<td class=rtbar><table border="0" cellpadding="0" cellspacing="0" align="right"><tr>' +
   	'<td class=rtbar>' + home.print() + '</td></tr></table></td>' +
   	'</tr></table>';

   	return toolbar;
}

/**
 * Return an non-sparse array from an array or an object. 
 */ 
function array(obj) {
    if (obj.length == undefined) {
		var a = new Array();
  		a[0] = obj;
  		return a;
  	}
  	else {
  		var a = new Array();
  		var n = 0;
  		for (var i in obj) {
  			a[n++] = obj[i];
  		}
  		return a;
  	}
}

/**
 * Dump a Javascript object to the console
 */
function dump(o) {
	for (f in o) {
		try {
			console.log(f + '=' + o[f]);
		} catch (e) {}
	}
}

/**
 * Return the content document of the given window.
 */
function content(win) {
	if (win.document != 'undefined' && win.document != null) {
		return win.document;
	} else if (win.contentDocument != 'undefined' && win.contentDocument != null) {
		return win.contentDocument;
	} else {
		return null;
	}
}

/**
 * Install a gadget
 */
function gadget(win, doc) {
	var ongadget = null;
	try {
		if (win.parent.ongadget != null && win.parent.ongadget != 'undefined') {
			ongadget = win.parent.ongadget;
		}
	} catch (e) {
	}
	if (ongadget != null) {
		return ongadget(win, doc);
	} else {
		return doc;
	}
}
