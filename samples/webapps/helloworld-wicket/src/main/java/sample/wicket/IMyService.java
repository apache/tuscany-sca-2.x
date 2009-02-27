package sample.wicket;


/**
 * Service interface for a simple "Hello World" app.
 * 
 * @author Alastair Maw
 */
public interface IMyService
{
    /**
     * Retrieves the text to say "Hello World".
     * 
     * @return "Hello World"
     */
    public String getHelloWorldText();
}