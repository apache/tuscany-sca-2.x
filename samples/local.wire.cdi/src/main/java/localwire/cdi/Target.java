package localwire.cdi;

import org.osoa.sca.annotations.Service;

/**
 * @version $Rev: 420007 $ $Date: 2006-07-07 15:41:37 -0700 (Fri, 07 Jul 2006) $
 */
@Service
public interface Target {
    String echo(String msg);
}
