package l2.authserver.crypt;


//import jonelo.jacksum.JacksumAPI;
//import jonelo.jacksum.algorithm.AbstractChecksum;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;


@Slf4j
public class PasswordHash {
  private final String name;
  Base64 base64 = new Base64();

  public PasswordHash(String name) {
    this.name = name;
  }

  public boolean compare(String password, String expected) {
    try {
      return this.encrypt(password).equalsIgnoreCase(expected);
    } catch (Exception e) {
      log.error("restore: eMessage={}, eClass={}", e.getMessage(), e.getClass());
      return false;
    }
  }

  public String encrypt(String password) throws Exception {
    log.info("encrypt: before={}", password);
    String encodedString = new String(base64.encode(password.getBytes()));
    log.info("encrypt: after={}", encodedString);
    return encodedString;
  }

  public static void main(String[] args) {
    String originalInput = "test input";
    Base64 base64 = new Base64();
    String encodedString = new String(base64.encode(originalInput.getBytes()));
    System.out.println(encodedString);
    String decodedString = new String(base64.decode(encodedString.getBytes()));
    System.out.println(decodedString);
  }
}
