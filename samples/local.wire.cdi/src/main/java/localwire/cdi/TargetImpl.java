package localwire.cdi;

import org.osoa.sca.annotations.Scope;

import localwire.cdi.Target;

/**
 * The component that is the target of the wire.
 * It simply exposes the "Target" service and has no properties or references.
 * 
 * @version $Rev: 420114 $ $Date: 2006-07-08 07:30:18 -0700 (Sat, 08 Jul 2006) $
 */
@Scope("MODULE")
public class TargetImpl implements Target {
    public String echo(String msg) {
        return "Echoing: " + msg;
    }
}
