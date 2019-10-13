//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import l2.commons.collections.LazyArrayList;
import l2.commons.dbutils.DbUtils;
import l2.commons.net.nio.impl.MMOClient;
import l2.commons.net.nio.impl.MMOConnection;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.GameServer;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.Msg;
import l2.gameserver.dao.CharacterDAO;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.instancemanager.BypassManager;
import l2.gameserver.instancemanager.BypassManager.BypassType;
import l2.gameserver.instancemanager.BypassManager.DecodedBypass;
import l2.gameserver.model.CharSelectInfoPackage;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.network.authcomm.AuthServerCommunication;
import l2.gameserver.network.authcomm.SessionKey;
import l2.gameserver.network.authcomm.gs2as.PlayerLogout;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.RequestNetPing;
import org.apache.commons.lang3.mutable.MutableLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GameClient extends MMOClient<MMOConnection<GameClient>> {
  private static final Logger _log = LoggerFactory.getLogger(GameClient.class);
  public static final String NO_IP = "?.?.?.?";
  public GameCrypt _crypt = null;
  public GameClient.GameClientState _state;
  private String _login;
  private Player _activeChar;
  private SessionKey _sessionKey;
  private String _ip = "?.?.?.?";
  private int revision = 0;
  private int serverId;
  private String _hwid;
  private List<Integer> _charSlotMapping = new ArrayList();
  private List<String> _bypasses = null;
  private List<String> _bypasses_bbs = null;
  private Map<Class<? extends L2GameClientPacket>, MutableLong> _lastIncomePacketTimeStamp = new ConcurrentHashMap();
  private SecondPasswordAuth _secondPasswordAuth;
  private boolean _isSecondPasswordAuthed = false;
  private int _failedPackets = 0;
  private int _unknownPackets = 0;
  public static int DEFAULT_PAWN_CLIPPING_RANGE = 2048;
  private int _pingTimestamp = 0;
  private int _ping = 0;
  private int _fps = 0;
  private int _pawnClippingRange = 0;
  private ScheduledFuture<?> _pingTaskFuture;

  public GameClient(MMOConnection<GameClient> con) {
    super(con);
    this._state = GameClient.GameClientState.CONNECTED;
    this._crypt = CGMHelper.isActive() ? CGMHelper.getInstance().createCrypt() : new GameCrypt();
    this._ip = con.getSocket().getInetAddress().getHostAddress();
  }

  protected void onDisconnection() {
    if (this._pingTaskFuture != null) {
      this._pingTaskFuture.cancel(true);
      this._pingTaskFuture = null;
    }

    this.setState(GameClient.GameClientState.DISCONNECTED);
    Player player = this.getActiveChar();
    this.setActiveChar((Player)null);
    if (player != null) {
      player.setNetConnection((GameClient)null);
      player.scheduleDelete();
    }

    if (this.getSessionKey() != null) {
      if (this.isAuthed()) {
        AuthServerCommunication.getInstance().removeAuthedClient(this.getLogin());
        AuthServerCommunication.getInstance().sendPacket(new PlayerLogout(this.getLogin()));
      } else {
        AuthServerCommunication.getInstance().removeWaitingClient(this.getLogin());
      }
    }

  }

  protected void onForcedDisconnection() {
  }

  public void markRestoredChar(int charslot) throws Exception {
    int objid = this.getObjectIdForSlot(charslot);
    if (objid >= 0) {
      if (this._activeChar != null && this._activeChar.getObjectId() == objid) {
        this._activeChar.setDeleteTimer(0);
      }

      Connection con = null;
      PreparedStatement statement = null;

      try {
        con = DatabaseFactory.getInstance().getConnection();
        statement = con.prepareStatement("UPDATE characters SET deletetime=0 WHERE obj_id=?");
        statement.setInt(1, objid);
        statement.execute();
      } catch (Exception var9) {
        _log.error("", var9);
      } finally {
        DbUtils.closeQuietly(con, statement);
      }

    }
  }

  public void markToDeleteChar(int charslot) throws Exception {
    int objid = this.getObjectIdForSlot(charslot);
    if (objid >= 0) {
      if (this._activeChar != null && this._activeChar.getObjectId() == objid) {
        this._activeChar.setDeleteTimer((int)(System.currentTimeMillis() / 1000L));
      }

      Connection con = null;
      PreparedStatement statement = null;

      try {
        con = DatabaseFactory.getInstance().getConnection();
        statement = con.prepareStatement("UPDATE characters SET deletetime=? WHERE obj_id=?");
        statement.setLong(1, (long)((int)(System.currentTimeMillis() / 1000L)));
        statement.setInt(2, objid);
        statement.execute();
      } catch (Exception var9) {
        _log.error("data error on update deletime char:", var9);
      } finally {
        DbUtils.closeQuietly(con, statement);
      }

    }
  }

  public void deleteCharacterInSlot(int charslot) throws Exception {
    if (this._activeChar == null) {
      int objid = this.getObjectIdForSlot(charslot);
      if (objid != -1) {
        this.deleteCharacterByCharacterObjId(objid);
      }
    }
  }

  public void deleteCharacterByCharacterObjId(int charObjId) {
    CharacterDAO.getInstance().deleteCharacterDataByObjId(charObjId);
  }

  public Player loadCharFromDisk(int charslot) {
    int objectId = this.getObjectIdForSlot(charslot);
    if (objectId == -1) {
      return null;
    } else {
      Player character = null;
      Player oldPlayer = GameObjectsStorage.getPlayer(objectId);
      if (oldPlayer != null) {
        if (oldPlayer.isInOfflineMode() || oldPlayer.isLogoutStarted()) {
          oldPlayer.kick();
          return null;
        }

        oldPlayer.sendPacket(Msg.ANOTHER_PERSON_HAS_LOGGED_IN_WITH_THE_SAME_ACCOUNT);
        GameClient oldClient = oldPlayer.getNetConnection();
        if (oldClient != null) {
          oldClient.setActiveChar((Player)null);
          oldClient.closeNow(false);
        }

        oldPlayer.setNetConnection(this);
        character = oldPlayer;
      }

      if (character == null) {
        character = Player.restore(objectId);
      }

      if (character != null) {
        this.setActiveChar(character);
      } else {
        _log.warn("could not restore obj_id: " + objectId + " in slot:" + charslot);
      }

      return character;
    }
  }

  public int getObjectIdForSlot(int charslot) {
    if (charslot >= 0 && charslot < this._charSlotMapping.size()) {
      return (Integer)this._charSlotMapping.get(charslot);
    } else {
      _log.warn(this.getLogin() + " tried to modify Character in slot " + charslot + " but no characters exits at that slot.");
      return -1;
    }
  }

  public int getSlotForObjectId(int objectId) {
    List<Integer> charSlotMapping = this._charSlotMapping;

    for(int slotIdx = 0; slotIdx < charSlotMapping.size(); ++slotIdx) {
      if (Integer.valueOf(objectId).equals(charSlotMapping.get(slotIdx))) {
        return slotIdx;
      }
    }

    return -1;
  }

  public SecondPasswordAuth getSecondPasswordAuth() {
    if (this.getLogin() != null && Config.USE_SECOND_PASSWORD_AUTH) {
      if (this._secondPasswordAuth == null) {
        this._secondPasswordAuth = new SecondPasswordAuth(this.getLogin());
      }

      return this._secondPasswordAuth;
    } else {
      return null;
    }
  }

  public boolean isSecondPasswordAuthed() {
    return this._secondPasswordAuth == null ? false : this._isSecondPasswordAuthed;
  }

  public void setSecondPasswordAuthed(boolean authed) {
    this._isSecondPasswordAuthed = authed;
  }

  public Player getActiveChar() {
    return this._activeChar;
  }

  public SessionKey getSessionKey() {
    return this._sessionKey;
  }

  public String getLogin() {
    return this._login;
  }

  public void setLoginName(String loginName) {
    this._login = loginName;
  }

  public void setActiveChar(Player player) {
    this._activeChar = player;
    if (player != null) {
      player.setNetConnection(this);
    }

  }

  public void setSessionId(SessionKey sessionKey) {
    this._sessionKey = sessionKey;
  }

  public void setCharSelection(CharSelectInfoPackage[] chars) {
    this._charSlotMapping.clear();
    CharSelectInfoPackage[] var2 = chars;
    int var3 = chars.length;

    for(int var4 = 0; var4 < var3; ++var4) {
      CharSelectInfoPackage element = var2[var4];
      int objectId = element.getObjectId();
      this._charSlotMapping.add(objectId);
    }

  }

  public void setCharSelection(int c) {
    this._charSlotMapping.clear();
    this._charSlotMapping.add(c);
  }

  public int getRevision() {
    return this.revision;
  }

  public void setRevision(int revision) {
    this.revision = revision;
  }

  public int getServerId() {
    return this.serverId;
  }

  public void setServerId(int serverId) {
    this.serverId = serverId;
  }

  public String getHwid() {
    return this._hwid;
  }

  public void setHwid(String hwid) {
    this._hwid = hwid;
  }

  private List<String> getStoredBypasses(boolean bbs) {
    if (bbs) {
      if (this._bypasses_bbs == null) {
        this._bypasses_bbs = new LazyArrayList();
      }

      return this._bypasses_bbs;
    } else {
      if (this._bypasses == null) {
        this._bypasses = new LazyArrayList();
      }

      return this._bypasses;
    }
  }

  public void cleanBypasses(boolean bbs) {
    List<String> bypassStorage = this.getStoredBypasses(bbs);
    synchronized(bypassStorage) {
      bypassStorage.clear();
    }
  }

  public String encodeBypasses(String htmlCode, boolean bbs) {
    List<String> bypassStorage = this.getStoredBypasses(bbs);
    synchronized(bypassStorage) {
      return BypassManager.encode(htmlCode, bypassStorage, bbs);
    }
  }

  public DecodedBypass decodeBypass(String bypass) {
    BypassType bpType = BypassManager.getBypassType(bypass);
    boolean bbs = bpType == BypassType.ENCODED_BBS || bpType == BypassType.SIMPLE_BBS;
    List<String> bypassStorage = this.getStoredBypasses(bbs);
    if (bpType != BypassType.ENCODED && bpType != BypassType.ENCODED_BBS) {
      if (bpType != BypassType.SIMPLE && bpType != BypassType.SIMPLE_BBS) {
        _log.warn("Direct access to bypass: " + bypass + " / " + this.toString());
        return null;
      } else {
        return (new DecodedBypass(bypass, bbs)).trim();
      }
    } else {
      return BypassManager.decode(bypass, bypassStorage, bbs, this);
    }
  }

  public long getLastIncomePacketTimeStamp(Class<? extends L2GameClientPacket> pktCls) {
    MutableLong theVal = (MutableLong)this._lastIncomePacketTimeStamp.get(pktCls);
    if (theVal == null) {
      this._lastIncomePacketTimeStamp.put(pktCls, theVal = new MutableLong(0L));
    }

    return theVal.longValue();
  }

  public void setLastIncomePacketTimeStamp(Class<? extends L2GameClientPacket> pktCls, long val) {
    MutableLong theVal = (MutableLong)this._lastIncomePacketTimeStamp.get(pktCls);
    if (theVal == null) {
      this._lastIncomePacketTimeStamp.put(pktCls, theVal = new MutableLong(0L));
    }

    theVal.setValue(val);
  }

  public boolean encrypt(ByteBuffer buf, int size) {
    this._crypt.encrypt(buf.array(), buf.position(), size);
    buf.position(buf.position() + size);
    return true;
  }

  public boolean decrypt(ByteBuffer buf, int size) {
    boolean ret = this._crypt.decrypt(buf.array(), buf.position(), size);
    return ret;
  }

  public void sendPacket(L2GameServerPacket gsp) {
    if (this.isConnected()) {
      if (gsp instanceof NpcHtmlMessage) {
        NpcHtmlMessage npcHtmlMessage = (NpcHtmlMessage)gsp;
        npcHtmlMessage.processHtml(this);
      }

      this.getConnection().sendPacket(gsp);
    }
  }

  public void sendPacket(L2GameServerPacket... gsps) {
    if (this.isConnected()) {
      L2GameServerPacket[] var2 = gsps;
      int var3 = gsps.length;

      for(int var4 = 0; var4 < var3; ++var4) {
        L2GameServerPacket gsp = var2[var4];
        if (gsp instanceof NpcHtmlMessage) {
          NpcHtmlMessage npcHtmlMessage = (NpcHtmlMessage)gsp;
          npcHtmlMessage.processHtml(this);
        }
      }

      this.getConnection().sendPacket(gsps);
    }
  }

  public void sendPackets(List<L2GameServerPacket> gsps) {
    if (this.isConnected()) {
      Iterator var2 = gsps.iterator();

      while(var2.hasNext()) {
        L2GameServerPacket gsp = (L2GameServerPacket)var2.next();
        if (gsp instanceof NpcHtmlMessage) {
          NpcHtmlMessage npcHtmlMessage = (NpcHtmlMessage)gsp;
          npcHtmlMessage.processHtml(this);
        }
      }

      this.getConnection().sendPackets(gsps);
    }
  }

  public void close(L2GameServerPacket gsp) {
    if (this.isConnected()) {
      if (gsp instanceof NpcHtmlMessage) {
        NpcHtmlMessage npcHtmlMessage = (NpcHtmlMessage)gsp;
        npcHtmlMessage.processHtml(this);
      }

      this.getConnection().close(gsp);
    }
  }

  public String getIpAddr() {
    return this._ip;
  }

  public byte[] enableCrypt() {
    byte[] key = CGMHelper.isActive() ? CGMHelper.getInstance().getRandomKey() : BlowFishKeygen.getRandomKey();
    this._crypt.setKey(key);
    return key;
  }

  public GameClient.GameClientState getState() {
    return this._state;
  }

  public void setState(GameClient.GameClientState state) {
    this._state = state;
    switch(state) {
      case AUTHED:
        this.onPing(0, 0, DEFAULT_PAWN_CLIPPING_RANGE);
      default:
    }
  }

  public void onPacketReadFail() {
    if (this._failedPackets++ >= 10) {
      _log.warn("Too many client packet fails, connection closed : " + this);
      this.closeNow(true);
    }

  }

  public void onUnknownPacket() {
    if (this._unknownPackets++ >= 10) {
      _log.warn("Too many client unknown packets, connection closed : " + this);
      this.closeNow(true);
    }

  }

  public String toString() {
    return this._state + " IP: " + this.getIpAddr() + (this._login == null ? "" : " Account: " + this._login) + (this._activeChar == null ? "" : " Player : " + this._activeChar);
  }

  public void onPing(int timestamp, int fps, int pawnClipRange) {
    if (this._pingTimestamp == 0 || this._pingTimestamp == timestamp) {
      long nowMs = System.currentTimeMillis();
      long serverStartTimeMs = GameServer.getInstance().getServerStartTime();
      this._ping = this._pingTimestamp > 0 ? (int)(nowMs - serverStartTimeMs - (long)timestamp) : 0;
      this._fps = fps;
      this._pawnClippingRange = pawnClipRange;
      this._pingTaskFuture = ThreadPoolManager.getInstance().schedule(new GameClient.PingTask(this, (SyntheticClass_1)null), 30000L);
    }

  }

  private final void doPing() {
    long nowMs = System.currentTimeMillis();
    long serverStartTimeMs = GameServer.getInstance().getServerStartTime();
    int timestamp = (int)(nowMs - serverStartTimeMs);
    this._pingTimestamp = timestamp;
    this.sendPacket((L2GameServerPacket)(new RequestNetPing(timestamp)));
  }

  public int getPing() {
    return this._ping;
  }

  public int getFps() {
    return this._fps;
  }

  public int getPawnClippingRange() {
    return this._pawnClippingRange;
  }

  private static class PingTask extends RunnableImpl {
    private final GameClient _client;

    private PingTask(GameClient client) {
      this._client = client;
    }

    public void runImpl() throws Exception {
      if (this._client != null && this._client.isConnected()) {
        this._client.doPing();
      }
    }
  }

  public static enum GameClientState {
    CONNECTED,
    AUTHED,
    IN_GAME,
    DISCONNECTED;

    private GameClientState() {
    }
  }
}
