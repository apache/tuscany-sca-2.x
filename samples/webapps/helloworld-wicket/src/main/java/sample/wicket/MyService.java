package sample.wicket;


/**
 * Implementation of IService.
 * 
 * @author Alastair Maw
 */
public class MyService implements IMyService
{

    /**
     * @see org.apache.wicket.examples.guice.service.IMyService#getHelloWorldText()
     */
    public String getHelloWorldText()
    {
        return "Hello World";
    }

}