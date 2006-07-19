package localwire;

import org.osoa.sca.annotations.Service;

/**
 * @version $Rev$ $Date$
 */
@Service
public interface Target {
    String echo(String msg);
}
