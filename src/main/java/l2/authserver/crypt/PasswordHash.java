package l2.authserver.crypt;


import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Slf4j
public class PasswordHash {

  private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  public static boolean compare(String requestPassword, String loadedPassword) {
    return passwordEncoder.matches(requestPassword, loadedPassword);
  }

  public static String encrypt(String password) {
    return passwordEncoder.encode(password);
  }

}
