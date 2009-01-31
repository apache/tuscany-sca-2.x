package sample;

import org.oasisopen.sca.annotation.Callback;
import org.oasisopen.sca.annotation.OneWay;
import org.oasisopen.sca.annotation.Remotable;

@Remotable
@Callback(HelloworldCallback.class)
public interface HelloworldService {

   @OneWay 
   void sayHello(String name);

}
