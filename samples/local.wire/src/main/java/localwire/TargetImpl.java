package localwire;

import org.osoa.sca.annotations.Scope;

/**
 * The component that is the target of the wire.
 * It simply exposes the "Target" service and has no properties or references.
 * 
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class TargetImpl implements Target {
    public String echo(String msg) {
        return "Echoing: " + msg;
    }
}
