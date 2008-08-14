package sample;

import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Service;

@Service(HelloworldService.class)
public class HelloworldServiceImpl implements HelloworldService {

    @Callback
    public HelloworldCallback callback;
    
    public void sayHello(final String name) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                for (int i=0; i<5; i++) {
                    callback.sayHelloCallback(i + "Hello " + name);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }});
        t.start();
    }

}
