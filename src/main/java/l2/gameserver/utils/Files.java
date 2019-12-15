//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

@Slf4j
public class Files {

  public Files() {
  }

  public static void writeFile(String path, String string) {
    try {
      FileUtils.writeStringToFile(new File(path), string, "UTF-8");
    } catch (IOException var3) {
      log.error("Error while saving file : " + path, var3);
    }

  }

  public static boolean copyFile(String srcFile, String destFile) {
    try {
      FileUtils.copyFile(new File(srcFile), new File(destFile), false);
      return true;
    } catch (IOException var3) {
      log.error("Error while copying file : " + srcFile + " to " + destFile, var3);
      return false;
    }
  }
}
