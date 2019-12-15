//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.commons.versioning;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

@Slf4j
public class Version {
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
      log.error("Unable to get soft information\nFile name '" + jarName.getAbsolutePath() + "' isn't a valid jar", var5);
    }

  }

  private void setVersionNumber(Attributes attrs) {
    String versionNumber = attrs.getValue("Implementation-Version");
    this._versionNumber = Objects.requireNonNullElse(versionNumber, "-1");

  }

  private void setRevisionNumber(Attributes attrs) {
    String revisionNumber = attrs.getValue("Implementation-Build");
    this._revisionNumber = Objects.requireNonNullElse(revisionNumber, "-1");

  }

  private void setBuildJdk(Attributes attrs) {
    String buildJdk = attrs.getValue("Build-Jdk");
    if (buildJdk != null) {
      this._buildJdk = buildJdk;
    } else {
      buildJdk = attrs.getValue("Created-By");
      this._buildJdk = Objects.requireNonNullElse(buildJdk, "-1");
    }

  }

  private void setBuildDate(Attributes attrs) {
    String buildDate = attrs.getValue("Build-Date");
    this._buildDate = Objects.requireNonNullElse(buildDate, "-1");

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
