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
 
function sayE4XHello(xmlIn) {

   var greeting = "e4xHello " + xmlIn..*::name;
   var xmlOut =  getXmlObject("http://helloworld","getGreetingsResponse");
   
   var ns = new Namespace("http://helloworld");
   xmlOut.ns::getGreetingsReturn = greeting;

   return xmlOut;
}

   
   
  function sayHello(name) {
   //create XML Request Object 
   var xmlIn =  getXmlObject("http://helloworld","getGreetings");
   var ns = new Namespace("http://helloworld");
   xmlIn.ns::name = name + " thro e4x reference";
   
   //invoke service thro service reference and obtain XML Response
   var xmlOut =  extHelloWorldService.sayE4XHello(xmlIn);
   
   //extract the content of response XML and return as string
   var greeting = "" + xmlOut..*::getGreetingsReturn;
   return greeting;
}
