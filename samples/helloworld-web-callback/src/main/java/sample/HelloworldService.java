package sample;

import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.OneWay;
import org.osoa.sca.annotations.Remotable;

@Remotable
@Callback(HelloworldCallback.class)
public interface HelloworldService {

   @OneWay 
   void sayHello(String name);

}
