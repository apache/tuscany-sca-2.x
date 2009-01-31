<%--
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
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
	<title>Tuscany Dojo JSON-RPC Customer Example</TITLE>

	<style type="text/css">
        @import "dojo/dijit/themes/soria/soria.css";
        @import "dojo/dojo/resources/dojo.css"
    </style>
    	            	
	<script type="text/javascript">
	        var djConfig = {
	    	        parseOnLoad: true,
	    	        baseRelativePath: "dojo",
	    	        isDebug: true,
	    	        debugContainerId: "dojoDebug"
		    };
			djConfig.usePlainJson=true ;
	        //djConfig.debugAtAllCosts = true;
	</script>
	
	<script type="text/javascript" src="dojo/dojo/dojo.js"></script>
	<script type="text/javascript" src="dojo/dijit/dijit.js"></script>
	
	<script type="text/javascript">
            dojo.require("dojo.parser");
	    	dojo.require("dojo.rpc.JsonService");
            dojo.require("dijit.form.ValidationTextBox");	
	    	dojo.require("dijit.form.Button");
	</script>
	
	<link rel="stylesheet" type="text/css" href="style.css" />
</head>

<body class="soria">

<h2>Tuscany Dojo JSON-RPC Customer Sample</h2>

Customer Name :<br>
<input type="text" id="name" size="30" value="Joe Smith" dojoType="dijit.form.TextBox" trim="true" propercase="true" required="true" />               
<input type="button" dojoType="dijit.form.Button" onclick="findCustomerByName()" />


<script type="text/javascript">
	function findCustomerByName() {
	    var name = document.getElementById("name").value;
	    customerService.findCustomerByName(name).addCallback(contentCallBack);;
	 }

	   	function contentCallBack(result) {
		   	alert(result.name);
   		//var handlerNode = document.getElementById("ReturnedContent");
   		//handlerNode.innerHTML = "<p>" + result + "</p>" ;
   	}
    
    var customerService = new dojo.rpc.JsonService("CustomerService?smd");
</script>

</body>
</html>
