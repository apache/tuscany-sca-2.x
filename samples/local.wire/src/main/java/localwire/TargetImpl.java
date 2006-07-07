package localwire;

import org.osoa.sca.annotations.Scope;

/**
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class TargetImpl implements Target {
    public String echo(String msg) {
        return "Echoing: " + msg;
    }
}
