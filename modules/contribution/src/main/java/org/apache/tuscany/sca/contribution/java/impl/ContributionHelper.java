package org.apache.tuscany.sca.contribution.java.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.Contribution;

public class ContributionHelper {

    public static List<URL> getNestedJarUrls(final Contribution contribution) throws IOException {
        List<URL> urls = new ArrayList<URL>();
        boolean isZipContribution = contribution.getLocation().endsWith(".zip");
        URI uri = URI.create(contribution.getLocation());
        boolean isFolderContribution = !isZipContribution && uri.getScheme().equals("file") && new File(uri).isDirectory();
        if (isZipContribution || isFolderContribution) {
            for (Artifact a : contribution.getArtifacts()) {
                if (a.getLocation().endsWith(".jar")) {
                    if (isZipContribution) {
                        urls.add(createTempJar(a, contribution));
                    } else {
                        urls.add(new URL(a.getLocation()));
                    }
                }
            }
        }
        return urls;
    }

    /**
     * URLClassLoader doesn't seem to work with URLs to jars within an archive so as a work around
     * copy the jar to a temp file and use the url to that.
     */
    private static URL createTempJar(Artifact artifact, Contribution contribution) throws IOException {
        FileOutputStream fileOutputStream = null;
        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(new File(URI.create(contribution.getLocation()))));
        try {
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                if (artifact.getLocation().endsWith(zipEntry.getName())) {

                    String tempName = ("tmp." + artifact.getURI().substring(0, artifact.getURI().length() - 3)).replace('/', '.');
                    File tempFile = File.createTempFile(tempName, ".jar");
                    tempFile.deleteOnExit();
                    fileOutputStream = new FileOutputStream(tempFile);

                    byte[] buf = new byte[2048];
                    int n;
                    while ((n = zipInputStream.read(buf, 0, buf.length)) > -1) {
                        fileOutputStream.write(buf, 0, n);
                    }

                    fileOutputStream.close();
                    zipInputStream.closeEntry();

                    return tempFile.toURI().toURL();

                }
                zipEntry = zipInputStream.getNextEntry();
            }
        } finally {
            zipInputStream.close();
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }
        
        throw new IllegalStateException();
    }
}
