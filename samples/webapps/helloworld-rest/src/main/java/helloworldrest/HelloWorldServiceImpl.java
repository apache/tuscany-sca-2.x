package helloworldrest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.oasisopen.sca.annotation.Scope;
import org.oasisopen.sca.annotation.Service;

@Service(HelloWorldService.class)
@Scope("Composite")
@Path("/helloworld")
public class HelloWorldServiceImpl implements HelloWorldService {

    private String name = new String("original!");

    @Path("/setname")
    @PUT
    @Consumes("text/plain")
    public void setName(String name) {
        this.name = name;

    }

    //http://<host>:<port>/helloworld-rest-webapp/HelloWorldService/helloworld/getname
    @Path("/getname")
    @GET
    @Produces("text/plain")
    public String getName() {
        return this.name;
    }

    @POST
    @Path("/postoperation/{name}/")
    @Consumes("text/plain")
    public void postOperationTest(@PathParam("name") String name) {
        this.name = name;
    }

}
