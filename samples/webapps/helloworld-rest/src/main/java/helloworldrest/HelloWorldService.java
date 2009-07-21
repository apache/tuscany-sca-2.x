package helloworldrest;

import org.oasisopen.sca.annotation.Remotable;

@Remotable
public interface HelloWorldService {

    public void setName(String name);

    public String getName();

    public void postOperationTest(String name);
}
