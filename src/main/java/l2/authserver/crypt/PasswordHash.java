package l2.authserver.crypt;


//import jonelo.jacksum.JacksumAPI;
//import jonelo.jacksum.algorithm.AbstractChecksum;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PasswordHash {
    private final String name;

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
//        AbstractChecksum checksum = JacksumAPI.getChecksumInstance(this.name);
//        checksum.setEncoding("BASE64");
//        checksum.update(password.getBytes());
//        return checksum.format("#CHECKSUM");
        log.error("PASSWORD ENCODE HARDOCRE!!!!!!!!!!!!!");
        return "";
    }
}
