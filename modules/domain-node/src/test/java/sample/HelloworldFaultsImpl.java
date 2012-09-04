package sample;

public class HelloworldFaultsImpl implements HelloworldFaults {

    @Override
    public String sayHello(String name) throws MyException {
        if ("beate".equals(name)) {
            throw new MyException("Bad Beate");
        }
        if ("bang".equals(name)) {
            throw new RuntimeException("got bang");
        }
        return "Hello " + name;
    }

}
