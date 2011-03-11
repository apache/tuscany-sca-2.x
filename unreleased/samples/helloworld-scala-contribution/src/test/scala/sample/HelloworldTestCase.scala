package samples

import org.junit._
import Assert._
import org.apache.tuscany.sca._

@Test
class HelloworldTestCase {

   @Test
   def testSayHello() = {
       var node = TuscanyRuntime.runComposite("helloworld.composite", "target/classes");
       try {
         var helloworld = node.getService(classOf[sample.Helloworld], "HelloworldComponent");
         assertEquals("Hello Amelia", helloworld.sayHello("Amelia"));
      } finally {
         node.stop();        
      }
   }

}


