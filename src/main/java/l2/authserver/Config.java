package l2.authserver;

import l2.authserver.crypt.PasswordHash;
import l2.authserver.crypt.ScrambledKeyPair;
import l2.commons.configuration.ExProperties;
import l2.commons.util.Rnd;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.KeyPairGenerator;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.*;

@Slf4j
public class Config {
  public static final String LOGIN_CONFIGURATION_FILE = "/config/authserver.properties";
  public static final String SERVER_NAMES_FILE = "/config/servername.xml";
  public static final String PROXY_SERVERS_FILE = "/config/proxyservers.xml";
  public static String LOGIN_HOST;
  public static int PORT_LOGIN;
  public static String GAME_SERVER_LOGIN_HOST;
  public static int GAME_SERVER_LOGIN_PORT;
  public static long GAME_SERVER_PING_DELAY;
  public static int GAME_SERVER_PING_RETRY;
  public static String DATABASE_HOST;
  public static int DATABASE_PORT;
  public static String DATABASE_NAME;
  public static String DATABASE_USER;
  public static String DATABASE_PASS;
  public static int DATABASE_MAX_CONN;
  public static int DATABASE_TIMEOUT;
  public static String DEFAULT_PASSWORD_HASH;
  public static String LEGACY_PASSWORD_HASH;
  public static int LOGIN_BLOWFISH_KEYS;
  public static int LOGIN_RSA_KEYPAIRS;
  public static boolean ACCEPT_NEW_GAMESERVER;
  public static boolean AUTO_CREATE_ACCOUNTS;
  public static String ANAME_TEMPLATE;
  public static String APASSWD_TEMPLATE;
  public static final Map<Integer, String> SERVER_NAMES = new HashMap<Integer, String>();
  public static final long LOGIN_TIMEOUT = 60000L;
  public static int LOGIN_TRY_BEFORE_BAN;
  public static long LOGIN_TRY_TIMEOUT;
  public static long IP_BAN_TIME;
  public static Set<String> WHITE_IPS = new HashSet<String>();
  private static ScrambledKeyPair[] keyPairs;
  private static byte[][] blowfishKeys;
  /**
   * this field is ignored, see PasswordHash see
   */
  public static PasswordHash DEFAULT_CRYPT;
  public static PasswordHash[] LEGACY_CRYPT;
  public static boolean LOGIN_LOG;
  public static Config.ProxyServerConfig[] PROXY_SERVERS_CONFIGS;
  public static String RESTART_AT_TIME;

  private Config() {
  }

  static void load() {
    loadConfiguration();
    loadServerNames();
    loadServerProxies();
  }

  static void initCrypt() throws Throwable {
    DEFAULT_CRYPT = new PasswordHash(DEFAULT_PASSWORD_HASH);
    List<PasswordHash> legacy = new ArrayList<PasswordHash>();
    String[] var1 = LEGACY_PASSWORD_HASH.split(";");
    int var2 = var1.length;

    int i;
    for (i = 0; i < var2; ++i) {
      String method = var1[i];
      if (!method.equalsIgnoreCase(DEFAULT_PASSWORD_HASH)) {
        legacy.add(new PasswordHash(method));
      }
    }

    LEGACY_CRYPT = legacy.toArray(new PasswordHash[0]);

    log.info("initCrypt: Loaded ={} as default crypt ", DEFAULT_PASSWORD_HASH);

    keyPairs = new ScrambledKeyPair[LOGIN_RSA_KEYPAIRS];
    KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
    RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(1024, RSAKeyGenParameterSpec.F4);
    keygen.initialize(spec);

    for (i = 0; i < keyPairs.length; ++i) {
      keyPairs[i] = new ScrambledKeyPair(keygen.generateKeyPair());
    }

    log.info("initCrypt: Cached ={} KeyPairs for RSA communication ", keyPairs.length);
    blowfishKeys = new byte[LOGIN_BLOWFISH_KEYS][16];

    for (i = 0; i < blowfishKeys.length; ++i) {
      for (int j = 0; j < blowfishKeys[i].length; ++j) {
        blowfishKeys[i][j] = (byte) (Rnd.get(255) + 1);
      }
    }

    log.info("initCrypt: Stored={} keys for Blowfish communication ", blowfishKeys.length);
  }

  private static void loadServerNames() {
    SERVER_NAMES.clear();

    try {
      SAXReader reader = new SAXReader(true);
      URL resource = Config.class.getResource(SERVER_NAMES_FILE);
      Document document = reader.read(resource);
      Element root = document.getRootElement();
      Iterator itr = root.elementIterator();

      while (itr.hasNext()) {
        Element node = (Element) itr.next();
        if (node.getName().equalsIgnoreCase("server")) {
          Integer id = Integer.valueOf(node.attributeValue("id"));
          String name = node.attributeValue("name");
          SERVER_NAMES.put(id, name);
        }
      }

      log.info("loadServerNames: Loaded={} server names ", SERVER_NAMES.size());
    } catch (Exception e) {
      log.error("loadServerNames: eMessage={}, eClass={}", e.getMessage(), e.getClass());
    }

  }

  private static void loadServerProxies() {
    ArrayList<ProxyServerConfig> proxyServersConfigs = new ArrayList<ProxyServerConfig>();

    try {
      SAXReader reader = new SAXReader(true);
      URL resource = Config.class.getResource(PROXY_SERVERS_FILE);
      Document document = reader.read(resource);
      Element root = document.getRootElement();
      Iterator itr = root.elementIterator();

      while (itr.hasNext()) {
        Element node = (Element) itr.next();
        if (node.getName().equalsIgnoreCase("proxyServer")) {
          int origSrvId = Integer.parseInt(node.attributeValue("origId"));
          int proxySrvId = Integer.parseInt(node.attributeValue("proxyId"));
          String proxyHost = node.attributeValue("proxyHost");
          int proxyPort = Integer.parseInt(node.attributeValue("proxyPort"));
          Config.ProxyServerConfig psc = new Config.ProxyServerConfig(origSrvId, proxySrvId, proxyHost, proxyPort);
          proxyServersConfigs.add(psc);
        }
      }
    } catch (Exception e) {
      log.error("loadServerProxies: eMessage={}, eClass={}", e.getMessage(), e.getClass());
    }

    PROXY_SERVERS_CONFIGS = proxyServersConfigs.toArray(new ProxyServerConfig[0]);
  }

  private static void loadConfiguration() {
    ExProperties serverSettings = loadClassPath();

    LOGIN_HOST = serverSettings.getProperty("LoginserverHostname", "127.0.0.1");
    PORT_LOGIN = serverSettings.getProperty("LoginserverPort", 2106);
    GAME_SERVER_LOGIN_HOST = serverSettings.getProperty("LoginHost", "127.0.0.1");
    GAME_SERVER_LOGIN_PORT = serverSettings.getProperty("LoginPort", 9014);
    DATABASE_HOST = serverSettings.getProperty("DatabaseHost", "127.0.0.1");
    DATABASE_PORT = serverSettings.getProperty("DatabasePort", 3306);
    DATABASE_NAME = serverSettings.getProperty("DatabaseName", "l2db");
    DATABASE_USER = serverSettings.getProperty("DatabaseUser", "root");
    DATABASE_PASS = serverSettings.getProperty("DatabasePassword", "");
    DATABASE_MAX_CONN = serverSettings.getProperty("DatabaseMaxConnections", 8);
    DATABASE_TIMEOUT = serverSettings.getProperty("DatabaseConnectionTimeout", 30);
    LOGIN_BLOWFISH_KEYS = serverSettings.getProperty("BlowFishKeys", 20);
    LOGIN_RSA_KEYPAIRS = serverSettings.getProperty("RSAKeyPairs", 10);
    ACCEPT_NEW_GAMESERVER = serverSettings.getProperty("AcceptNewGameServer", true);
    DEFAULT_PASSWORD_HASH = serverSettings.getProperty("PasswordHash", "sha1");
    LEGACY_PASSWORD_HASH = serverSettings.getProperty("LegacyPasswordHash", "whirlpool2");
    AUTO_CREATE_ACCOUNTS = serverSettings.getProperty("AutoCreateAccounts", true);
    ANAME_TEMPLATE = serverSettings.getProperty("AccountTemplate.regexp", "[A-Za-z0-9]{4,14}");
    APASSWD_TEMPLATE = serverSettings.getProperty("PasswordTemplate.regexp", "[A-Za-z0-9]{4,16}");
    LOGIN_TRY_BEFORE_BAN = serverSettings.getProperty("LoginTryBeforeBan", 10);
    LOGIN_TRY_TIMEOUT = (long) serverSettings.getProperty("LoginTryTimeout", 5) * 1000L;
    IP_BAN_TIME = (long) serverSettings.getProperty("IpBanTime", 300) * 1000L;
    WHITE_IPS.addAll(Arrays.asList(serverSettings.getProperty("WhiteIpList", new String[]{"127.0.0.1"})));
    GAME_SERVER_PING_DELAY = (long) serverSettings.getProperty("GameServerPingDelay", 30) * 1000L;
    GAME_SERVER_PING_RETRY = serverSettings.getProperty("GameServerPingRetry", 4);
    LOGIN_LOG = serverSettings.getProperty("LoginLog", true);
    RESTART_AT_TIME = serverSettings.getProperty("AutoRestartAt", "");
  }

  private static ExProperties loadClassPath() {
    String resource = Config.class.getResource(Config.LOGIN_CONFIGURATION_FILE).getFile();
    File file = new File(resource);
    return load(file);
  }

  private static ExProperties load(File file) {
    ExProperties result = new ExProperties();

    try {
      result.load(file);
    } catch (IOException e) {
      log.error("load: eMessage={}, eClass={}", e.getMessage(), e.getClass());
    }
    return result;
  }

  public static ScrambledKeyPair getScrambledRSAKeyPair() {
    return keyPairs[Rnd.get(keyPairs.length)];
  }

  public static byte[] getBlowfishKey() {
    return blowfishKeys[Rnd.get(blowfishKeys.length)];
  }

  @Getter
  static class ProxyServerConfig {
    private final int origServerId;
    private final int proxyServerId;
    private final String porxyHost;
    private final int proxyPort;

    ProxyServerConfig(int origServerId, int proxyServerId, String porxyHost, int proxyPort) {
      this.origServerId = origServerId;
      this.proxyServerId = proxyServerId;
      this.porxyHost = porxyHost;
      this.proxyPort = proxyPort;
    }

  }
}
