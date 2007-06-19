

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.sca.contribution.resolver.ModelResolver;


/**
 * A default implementation of an artifact resolver, based on a map.
 *
 * @version $Rev$ $Date$
 */
public class TestModelResolver implements ModelResolver {
    private static final long serialVersionUID = -7826976465762296634L;
    
    private Map<Object, Object> map = new HashMap<Object, Object>();
    
    public TestModelResolver(ClassLoader classLoader) {
    }

    public <T> T resolveModel(Class<T> modelClass, T unresolved) {
        Object resolved = map.get(unresolved);
        if (resolved != null) {
            
            // Return the resolved object
            return modelClass.cast(resolved);
            
        } else {
            
            // Return the unresolved object
            return unresolved;
        }
    }
    
    public void addModel(Object resolved) {
        map.put(resolved, resolved);
    }
    
    public Object removeModel(Object resolved) {
        return map.remove(resolved);
    }
    
}
