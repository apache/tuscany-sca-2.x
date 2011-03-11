package sample

import org.oasisopen.sca.annotation.Remotable;

@Remotable
trait Helloworld {
  def sayHello(name : String):String
}
