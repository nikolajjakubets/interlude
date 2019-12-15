package l2.authserver.network.gamecomm;

import l2.authserver.Config;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
@Slf4j
public class GameServer {
  private int id;
  private String internalHost;
  private String externalHost;
  private InetAddress internalAddr;
  private InetAddress externalAddr;
  private volatile int[] ports = new int[]{7777};
  private int serverType;
  private int ageLimit;
  private int protocol;
  private boolean isOnline;
  private boolean isPvp;
  private boolean isShowingBrackets;
  private boolean isGmOnly;
  private int maxPlayers;
  private GameServerConnection conn;
  private boolean isAuthed;
  private AtomicInteger port = new AtomicInteger(0);
  private volatile int playersIngame;

  public GameServer(GameServerConnection conn) {
    this.conn = conn;
  }

  public GameServer(int id) {
    this.id = id;
  }

  public void setConnection(GameServerConnection conn) {
    this.conn = conn;
  }

  public GameServerConnection getConnection() {
    return this.conn;
  }

  public InetAddress getInternalHost() throws UnknownHostException {
    return this.internalAddr != null ? this.internalAddr : (this.internalAddr = InetAddress.getByName(this.internalHost));
  }

  public void setInternalHost(String internalHost) {
    if (internalHost.equals("*")) {
      internalHost = this.getConnection().getIpAddress();
    }

    this.internalHost = internalHost;
    this.internalAddr = null;
  }

  public void setExternalHost(String externalHost) {
    if (externalHost.equals("*")) {
      externalHost = this.getConnection().getIpAddress();
    }

    this.externalHost = externalHost;
    this.externalAddr = null;
  }

  public InetAddress getExternalHost() throws UnknownHostException {
    return this.externalAddr != null ? this.externalAddr : (this.externalAddr = InetAddress.getByName(this.externalHost));
  }

  public int getPort() {
    return ports[(this.port.incrementAndGet() & 2147483647) % ports.length];
  }

  public int getOnline() {
    return this.playersIngame;
  }

  public void addAccount() {
    ++this.playersIngame;
  }

  public void removeAccount() {
    --this.playersIngame;
  }

  public void setDown() {
    this.setAuthed(false);
    this.setConnection(null);
    this.setOnline(false);
  }

  public String getName() {
    return Config.SERVER_NAMES.get(this.getId());
  }

  public void sendPacket(SendablePacket packet) {
    GameServerConnection conn = this.getConnection();
    if (conn != null) {
      conn.sendPacket(packet);
    }
  }

  public boolean isOnline() {
    return this.isOnline;
  }

}
