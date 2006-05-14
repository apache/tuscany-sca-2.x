
function process(inXML) {

   var greeting = "hello " + inXML..*::in0;
   var outXML = 
      <helloworldaxis:getGreetingsResponse xmlns:helloworldaxis="http://helloworld.samples.tuscany.apache.org">
         <helloworldaxis:getGreetingsReturn>{ greeting }</helloworldaxis:getGreetingsReturn>
      </helloworldaxis:getGreetingsResponse>;

   return outXML;
}
