package helloworldrest;

import org.osoa.sca.annotations.Remotable;

@Remotable
public interface HelloWorldService {

    public void setName(String name);
    public String getName();
    public void postOperationTest(String name);
}
