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
 
function Tool(name, href) {
	this.name = name;
	this.href = href;
}

Tool.prototype.print = function() {
    var loc = '' + location;
	if (loc.match(this.href) == null) {
		return '<a href="' + this.href + '" style="color: blue">' + this.name + '</a>';
	} else {
		return '<span>' + this.name + '</span>';
	}
}

function toolbar() {

	var toolbar = '<table border="0" cellspacing="0" cellpadding="0" width="100%" ' +
	' style="padding-bottom: 2px; border-bottom: 1px solid blue"><tr>' + 
	'<td>' +
	'<table border="0" cellspacing="0" cellpadding="0"><tr>';
   
	for (var i = 0; i < tools.length; i++) {
	   toolbar = toolbar + '<td>' +tools[i].print() + '&nbsp;&nbsp;&nbsp;</td>'
   	}
   
   	toolbar = toolbar + '</tr></table>' + 
   	'</td>' +
   	'<td>' +
   	'<table border="0" cellspacing="0" cellpadding="0" align="right"><tr>' +
   	'<td>' + home.print() + '</td>' +
   	'</tr></table>' +
   	'</td>' + 
   	'</tr></table>';

	document.getElementById('toolbar').innerHTML = toolbar;
}

function array(obj) {
    if (obj.length == undefined) {
		var a = new Array();
  		a[0] = obj;
  		return a;
  	}
  	else {
  		return obj;
  	}
}

var tools = new Array();
tools[0] = new Tool("Contributions", "/ui/workspace");
tools[1] = new Tool("Composites", "/ui/composite");
tools[2] = new Tool("Nodes", "/ui/cloud");
tools[3] = new Tool("Files", "/ui/files");

var home = new Tool("Home", "/ui/home");
