/**
 * 
 */
package org.apache.tuscany.services.maven;

import java.net.URL;
import java.net.URLClassLoader;

import org.apache.tuscany.spi.services.artifact.Artifact;

import junit.framework.TestCase;

/**
 * @author Administrator
 *
 */
public class WarRepositoryHelperTestCase extends TestCase {

    /**
     * @param arg0
     */
    public WarRepositoryHelperTestCase(String arg0) {
        super(arg0);
    }

    /**
     * Test method for {@link org.apache.tuscany.services.maven.WarRepositoryHelper#WarRepositoryHelper(java.net.URL)}.
     */
    public void testWarRepositoryHelper() {

        /*URL warUrl = getClass().getClassLoader().getResource("webapp.war");
        URLClassLoader urlc = new URLClassLoader(new URL[] {warUrl});
        
        URL repoUrl = urlc.getResource("WEB-INF/tuscany/repository/");
        WarRepositoryHelper warRepositoryHelper = new WarRepositoryHelper(repoUrl);
        assertNotNull(warRepositoryHelper);*/
        
    }

    /**
     * Test method for {@link org.apache.tuscany.services.maven.WarRepositoryHelper#WarRepositoryHelper(java.net.URL)}.
     */
    public void testResolveTransitively() {

        /*URL warUrl = getClass().getClassLoader().getResource("webapp.war");
        URLClassLoader urlc = new URLClassLoader(new URL[] {warUrl});
        
        URL repoUrl = urlc.getResource("WEB-INF/tuscany/repository/");
        WarRepositoryHelper warRepositoryHelper = new WarRepositoryHelper(repoUrl);
        
        Artifact artifact = new Artifact();
        artifact.setGroup("commons-httpclient");
        artifact.setName("commons-httpclient");
        artifact.setVersion("3.0");
        
        warRepositoryHelper.resolveTransitively(artifact);
        assertEquals(4, artifact.getUrls().size());*/
        
    }

}
