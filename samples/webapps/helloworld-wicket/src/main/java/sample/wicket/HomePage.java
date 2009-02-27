package sample.wicket;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.oasisopen.sca.annotation.Reference;

/**
 * Everybody's favorite example (Hello World), modified to use Guice.
 * 
 * @author Alastair Maw
 */
public class HomePage extends WebPage
{
    @Reference
    IMyService service;

    private String labelValue = "<not yet initialized>";

    /**
     * Constructor
     */
    public HomePage()
    {
        add(new Link("link")
        {
            /**
             * @see org.apache.wicket.markup.html.link.Link#onClick()
             */
            @Override
            public void onClick()
            {
                labelValue = service.getHelloWorldText();
            }
        });
        add(new Label("message", new AbstractReadOnlyModel<String>()
        {

            @Override
            public String getObject()
            {
                return labelValue;
            }

        }));
    }
}