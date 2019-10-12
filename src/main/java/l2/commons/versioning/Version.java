//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.commons.versioning;

import java.io.File;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Version {
    private static final Logger _log = LoggerFactory.getLogger(Version.class);
    private String _revisionNumber = "exported";
    private String _versionNumber = "-1";
    private String _buildDate = "";
    private String _buildJdk = "";

    public Version(Class<?> c) {
        File jarName = null;

        try {
            jarName = Locator.getClassSource(c);
            JarFile jarFile = new JarFile(jarName);
            Attributes attrs = jarFile.getManifest().getMainAttributes();
            this.setBuildJdk(attrs);
            this.setBuildDate(attrs);
            this.setRevisionNumber(attrs);
            this.setVersionNumber(attrs);
        } catch (IOException var5) {
            _log.error("Unable to get soft information\nFile name '" + (jarName == null ? "null" : jarName.getAbsolutePath()) + "' isn't a valid jar", var5);
        }

    }

    private void setVersionNumber(Attributes attrs) {
        String versionNumber = attrs.getValue("Implementation-Version");
        if (versionNumber != null) {
            this._versionNumber = versionNumber;
        } else {
            this._versionNumber = "-1";
        }

    }

    private void setRevisionNumber(Attributes attrs) {
        String revisionNumber = attrs.getValue("Implementation-Build");
        if (revisionNumber != null) {
            this._revisionNumber = revisionNumber;
        } else {
            this._revisionNumber = "-1";
        }

    }

    private void setBuildJdk(Attributes attrs) {
        String buildJdk = attrs.getValue("Build-Jdk");
        if (buildJdk != null) {
            this._buildJdk = buildJdk;
        } else {
            buildJdk = attrs.getValue("Created-By");
            if (buildJdk != null) {
                this._buildJdk = buildJdk;
            } else {
                this._buildJdk = "-1";
            }
        }

    }

    private void setBuildDate(Attributes attrs) {
        String buildDate = attrs.getValue("Build-Date");
        if (buildDate != null) {
            this._buildDate = buildDate;
        } else {
            this._buildDate = "-1";
        }

    }

    public String getRevisionNumber() {
        return this._revisionNumber;
    }

    public String getVersionNumber() {
        return this._versionNumber;
    }

    public String getBuildDate() {
        return this._buildDate;
    }

    public String getBuildJdk() {
        return this._buildJdk;
    }
}
