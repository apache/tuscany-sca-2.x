package mystripes.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.oasisopen.sca.annotation.Reference;

import sample.HelloworldService;

@UrlBinding("/Home.htm")
public class HomeActionBean extends BaseActionBean {

    @Reference
    HelloworldService service;

    @DefaultHandler
    public Resolution view() {
        return new ForwardResolution("/WEB-INF/jsp/home.jsp");
    }

    public String getHello() {
        return service.sayHello("world");
    }

}
