//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.instancemanager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import l2.gameserver.Config;
import l2.gameserver.dao.CharacterDAO;
import l2.gameserver.handler.petition.IPetitionHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.Say2;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.tables.GmListTable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PetitionManager implements IPetitionHandler {
  private static final Logger _log = LoggerFactory.getLogger(PetitionManager.class.getName());
  private static final PetitionManager _instance = new PetitionManager();
  private AtomicInteger _nextId = new AtomicInteger();
  private Map<Integer, PetitionManager.Petition> _pendingPetitions = new ConcurrentHashMap();
  private Map<Integer, PetitionManager.Petition> _completedPetitions = new ConcurrentHashMap();

  public static final PetitionManager getInstance() {
    return _instance;
  }

  private PetitionManager() {
    _log.info("Initializing PetitionManager");
  }

  public int getNextId() {
    return this._nextId.incrementAndGet();
  }

  public void clearCompletedPetitions() {
    int numPetitions = this.getPendingPetitionCount();
    this.getCompletedPetitions().clear();
    _log.info("PetitionManager: Completed petition data cleared. " + numPetitions + " petition(s) removed.");
  }

  public void clearPendingPetitions() {
    int numPetitions = this.getPendingPetitionCount();
    this.getPendingPetitions().clear();
    _log.info("PetitionManager: Pending petition queue cleared. " + numPetitions + " petition(s) removed.");
  }

  public boolean acceptPetition(Player respondingAdmin, int petitionId) {
    if (!this.isValidPetition(petitionId)) {
      return false;
    } else {
      PetitionManager.Petition currPetition = (PetitionManager.Petition)this.getPendingPetitions().get(petitionId);
      if (currPetition.getResponder() != null) {
        return false;
      } else {
        currPetition.setResponder(respondingAdmin);
        currPetition.setState(PetitionManager.PetitionState.In_Process);
        currPetition.sendPetitionerPacket(new SystemMessage(406));
        currPetition.sendResponderPacket((new SystemMessage(389)).addNumber(currPetition.getId()));
        currPetition.sendResponderPacket((new SystemMessage(394)).addString(currPetition.getPetitioner().getName()));
        return true;
      }
    }
  }

  public boolean cancelActivePetition(Player player) {
    Iterator var2 = this.getPendingPetitions().values().iterator();

    PetitionManager.Petition currPetition;
    do {
      if (!var2.hasNext()) {
        return false;
      }

      currPetition = (PetitionManager.Petition)var2.next();
      if (currPetition.getPetitioner() != null && currPetition.getPetitioner().getObjectId() == player.getObjectId()) {
        return currPetition.endPetitionConsultation(PetitionManager.PetitionState.Petitioner_Cancel);
      }
    } while(currPetition.getResponder() == null || currPetition.getResponder().getObjectId() != player.getObjectId());

    return currPetition.endPetitionConsultation(PetitionManager.PetitionState.Responder_Cancel);
  }

  public void checkPetitionMessages(Player petitioner) {
    if (petitioner != null) {
      Iterator var2 = this.getPendingPetitions().values().iterator();

      while(var2.hasNext()) {
        PetitionManager.Petition currPetition = (PetitionManager.Petition)var2.next();
        if (currPetition != null && currPetition.getPetitioner() != null && currPetition.getPetitioner().getObjectId() == petitioner.getObjectId()) {
          Iterator var4 = currPetition.getLogMessages().iterator();

          while(var4.hasNext()) {
            Say2 logMessage = (Say2)var4.next();
            petitioner.sendPacket(logMessage);
          }

          return;
        }
      }
    }

  }

  public boolean endActivePetition(Player player) {
    if (!player.isGM()) {
      return false;
    } else {
      Iterator var2 = this.getPendingPetitions().values().iterator();

      PetitionManager.Petition currPetition;
      do {
        if (!var2.hasNext()) {
          return false;
        }

        currPetition = (PetitionManager.Petition)var2.next();
      } while(currPetition == null || currPetition.getResponder() == null || currPetition.getResponder().getObjectId() != player.getObjectId());

      return currPetition.endPetitionConsultation(PetitionManager.PetitionState.Completed);
    }
  }

  protected Map<Integer, PetitionManager.Petition> getCompletedPetitions() {
    return this._completedPetitions;
  }

  protected Map<Integer, PetitionManager.Petition> getPendingPetitions() {
    return this._pendingPetitions;
  }

  public int getPendingPetitionCount() {
    return this.getPendingPetitions().size();
  }

  public int getPlayerTotalPetitionCount(Player player) {
    if (player == null) {
      return 0;
    } else {
      int petitionCount = 0;
      Iterator var3 = this.getPendingPetitions().values().iterator();

      PetitionManager.Petition currPetition;
      while(var3.hasNext()) {
        currPetition = (PetitionManager.Petition)var3.next();
        if (currPetition != null && currPetition.getPetitioner() != null && currPetition.getPetitioner().getObjectId() == player.getObjectId()) {
          ++petitionCount;
        }
      }

      var3 = this.getCompletedPetitions().values().iterator();

      while(var3.hasNext()) {
        currPetition = (PetitionManager.Petition)var3.next();
        if (currPetition != null && currPetition.getPetitioner() != null && currPetition.getPetitioner().getObjectId() == player.getObjectId()) {
          ++petitionCount;
        }
      }

      return petitionCount;
    }
  }

  public boolean isPetitionPending() {
    Iterator var1 = this.getPendingPetitions().values().iterator();

    PetitionManager.Petition currPetition;
    do {
      if (!var1.hasNext()) {
        return false;
      }

      currPetition = (PetitionManager.Petition)var1.next();
    } while(currPetition == null || currPetition.getState() != PetitionManager.PetitionState.Pending);

    return true;
  }

  public boolean isPetitionInProcess() {
    Iterator var1 = this.getPendingPetitions().values().iterator();

    PetitionManager.Petition currPetition;
    do {
      if (!var1.hasNext()) {
        return false;
      }

      currPetition = (PetitionManager.Petition)var1.next();
    } while(currPetition == null || currPetition.getState() != PetitionManager.PetitionState.In_Process);

    return true;
  }

  public boolean isPetitionInProcess(int petitionId) {
    if (!this.isValidPetition(petitionId)) {
      return false;
    } else {
      PetitionManager.Petition currPetition = (PetitionManager.Petition)this.getPendingPetitions().get(petitionId);
      return currPetition.getState() == PetitionManager.PetitionState.In_Process;
    }
  }

  public boolean isPlayerInConsultation(Player player) {
    if (player != null) {
      Iterator var2 = this.getPendingPetitions().values().iterator();

      while(var2.hasNext()) {
        PetitionManager.Petition currPetition = (PetitionManager.Petition)var2.next();
        if (currPetition != null && currPetition.getState() == PetitionManager.PetitionState.In_Process && (currPetition.getPetitioner() != null && currPetition.getPetitioner().getObjectId() == player.getObjectId() || currPetition.getResponder() != null && currPetition.getResponder().getObjectId() == player.getObjectId())) {
          return true;
        }
      }
    }

    return false;
  }

  public boolean isPetitioningAllowed() {
    return Config.PETITIONING_ALLOWED;
  }

  public boolean isPlayerPetitionPending(Player petitioner) {
    if (petitioner != null) {
      Iterator var2 = this.getPendingPetitions().values().iterator();

      while(var2.hasNext()) {
        PetitionManager.Petition currPetition = (PetitionManager.Petition)var2.next();
        if (currPetition != null && currPetition.getPetitioner() != null && currPetition.getPetitioner().getObjectId() == petitioner.getObjectId()) {
          return true;
        }
      }
    }

    return false;
  }

  private boolean isValidPetition(int petitionId) {
    return this.getPendingPetitions().containsKey(petitionId);
  }

  public boolean rejectPetition(Player respondingAdmin, int petitionId) {
    if (!this.isValidPetition(petitionId)) {
      return false;
    } else {
      PetitionManager.Petition currPetition = (PetitionManager.Petition)this.getPendingPetitions().get(petitionId);
      if (currPetition.getResponder() != null) {
        return false;
      } else {
        currPetition.setResponder(respondingAdmin);
        return currPetition.endPetitionConsultation(PetitionManager.PetitionState.Responder_Reject);
      }
    }
  }

  public boolean sendActivePetitionMessage(Player player, String messageText) {
    Iterator var4 = this.getPendingPetitions().values().iterator();

    while(var4.hasNext()) {
      PetitionManager.Petition currPetition = (PetitionManager.Petition)var4.next();
      if (currPetition != null) {
        Say2 cs;
        if (currPetition.getPetitioner() != null && currPetition.getPetitioner().getObjectId() == player.getObjectId()) {
          cs = new Say2(player.getObjectId(), ChatType.PETITION_PLAYER, player.getName(), messageText);
          currPetition.addLogMessage(cs);
          currPetition.sendResponderPacket(cs);
          currPetition.sendPetitionerPacket(cs);
          return true;
        }

        if (currPetition.getResponder() != null && currPetition.getResponder().getObjectId() == player.getObjectId()) {
          cs = new Say2(player.getObjectId(), ChatType.PETITION_GM, player.getName(), messageText);
          currPetition.addLogMessage(cs);
          currPetition.sendResponderPacket(cs);
          currPetition.sendPetitionerPacket(cs);
          return true;
        }
      }
    }

    return false;
  }

  public void sendPendingPetitionList(Player activeChar) {
    StringBuilder htmlContent = new StringBuilder(600 + this.getPendingPetitionCount() * 300);
    htmlContent.append("<html><body><center><table width=270><tr><td width=45><button value=\"Main\" action=\"bypass -h admin_admin\" width=45 height=21 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td><td width=180><center>Petition Menu</center></td><td width=45><button value=\"Back\" action=\"bypass -h admin_admin\" width=45 height=21 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td></tr></table><br><table width=\"270\"><tr><td><table width=\"270\"><tr><td><button value=\"Reset\" action=\"bypass -h admin_reset_petitions\" width=\"80\" height=\"21\" back=\"sek.cbui94\" fore=\"sek.cbui94\"></td><td align=right><button value=\"Refresh\" action=\"bypass -h admin_view_petitions\" width=\"80\" height=\"21\" back=\"sek.cbui94\" fore=\"sek.cbui94\"></td></tr></table><br></td></tr>");
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    if (this.getPendingPetitionCount() == 0) {
      htmlContent.append("<tr><td>There are no currently pending petitions.</td></tr>");
    } else {
      htmlContent.append("<tr><td><font color=\"LEVEL\">Current Petitions:</font><br></td></tr>");
    }

    boolean color = true;
    int petcount = 0;
    Iterator var6 = this.getPendingPetitions().values().iterator();

    while(var6.hasNext()) {
      PetitionManager.Petition currPetition = (PetitionManager.Petition)var6.next();
      if (currPetition != null) {
        htmlContent.append("<tr><td width=\"270\"><table width=\"270\" cellpadding=\"2\" bgcolor=").append(color ? "131210" : "444444").append("><tr><td width=\"130\">").append(dateFormat.format(new Date(currPetition.getSubmitTime())));
        htmlContent.append("</td><td width=\"140\" align=right><font color=\"").append(currPetition.isPetitionerOnline() ? "00FF00" : "999999").append("\">").append(currPetition.getPetitionerName()).append("</font></td></tr>");
        htmlContent.append("<tr><td width=\"130\">");
        if (currPetition.getState() != PetitionManager.PetitionState.In_Process) {
          htmlContent.append("<table width=\"130\" cellpadding=\"2\"><tr><td><button value=\"View\" action=\"bypass -h admin_view_petition ").append(currPetition.getId()).append("\" width=\"50\" height=\"21\" back=\"sek.cbui94\" fore=\"sek.cbui94\"></td><td><button value=\"Reject\" action=\"bypass -h admin_reject_petition ").append(currPetition.getId()).append("\" width=\"50\" height=\"21\" back=\"sek.cbui94\" fore=\"sek.cbui94\"></td></tr></table>");
        } else {
          htmlContent.append("<font color=\"").append(currPetition.getResponder().isOnline() ? "00FF00" : "999999").append("\">").append(currPetition.getResponder().getName()).append("</font>");
        }

        htmlContent.append("</td>").append(currPetition.getTypeAsString()).append("<td width=\"140\" align=right>").append(currPetition.getTypeAsString()).append("</td></tr></table></td></tr>");
        color = !color;
        ++petcount;
        if (petcount > 10) {
          htmlContent.append("<tr><td><font color=\"LEVEL\">There is more pending petition...</font><br></td></tr>");
          break;
        }
      }
    }

    htmlContent.append("</table></center></body></html>");
    NpcHtmlMessage htmlMsg = new NpcHtmlMessage(0);
    htmlMsg.setHtml(htmlContent.toString());
    activeChar.sendPacket(htmlMsg);
  }

  public int submitPetition(Player petitioner, String petitionText, int petitionType) {
    PetitionManager.Petition newPetition = new PetitionManager.Petition(petitioner, petitionText, petitionType);
    int newPetitionId = newPetition.getId();
    this.getPendingPetitions().put(newPetitionId, newPetition);
    String msgContent = petitioner.getName() + " has submitted a new petition.";
    GmListTable.broadcastToGMs(new Say2(petitioner.getObjectId(), ChatType.CRITICAL_ANNOUNCE, "Petition System", msgContent));
    return newPetitionId;
  }

  public void viewPetition(Player activeChar, int petitionId) {
    if (activeChar.isGM()) {
      if (this.isValidPetition(petitionId)) {
        PetitionManager.Petition currPetition = (PetitionManager.Petition)this.getPendingPetitions().get(petitionId);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        NpcHtmlMessage html = new NpcHtmlMessage(0);
        html.setFile("admin/petition.htm");
        html.replace("%petition%", String.valueOf(currPetition.getId()));
        html.replace("%time%", dateFormat.format(new Date(currPetition.getSubmitTime())));
        html.replace("%type%", currPetition.getTypeAsString());
        html.replace("%petitioner%", currPetition.getPetitionerName());
        html.replace("%online%", currPetition.isPetitionerOnline() ? "00FF00" : "999999");
        html.replace("%text%", currPetition.getContent());
        activeChar.sendPacket(html);
      }
    }
  }

  public void handle(Player player, int typeId, String txt) {
    if (!Config.CAN_PETITION_TO_OFFLINE_GM && GmListTable.getAllGMs().size() == 0) {
      player.sendPacket(new SystemMessage(702));
    } else if (!getInstance().isPetitioningAllowed()) {
      player.sendPacket(new SystemMessage(381));
    } else if (getInstance().isPlayerPetitionPending(player)) {
      player.sendPacket(new SystemMessage(390));
    } else if (getInstance().getPendingPetitionCount() == Config.MAX_PETITIONS_PENDING) {
      player.sendPacket(new SystemMessage(602));
    } else {
      int totalPetitions = getInstance().getPlayerTotalPetitionCount(player) + 1;
      if (totalPetitions > Config.MAX_PETITIONS_PER_PLAYER) {
        player.sendPacket(new SystemMessage(733));
      } else if (txt.length() > 255) {
        player.sendPacket(new SystemMessage(971));
      } else if (typeId >= PetitionManager.PetitionType.values().length) {
        _log.warn("PetitionManager: Invalid petition type : " + typeId);
      } else {
        int petitionId = getInstance().submitPetition(player, txt, typeId);
        player.sendPacket((new SystemMessage(389)).addNumber(petitionId));
        player.sendPacket((new SystemMessage(730)).addNumber(totalPetitions).addNumber(Config.MAX_PETITIONS_PER_PLAYER - totalPetitions));
        player.sendPacket((new SystemMessage(601)).addNumber(getInstance().getPendingPetitionCount()));
      }
    }
  }

  private class Petition {
    private long _submitTime = System.currentTimeMillis();
    private long _endTime = -1L;
    private int _id;
    private PetitionManager.PetitionType _type;
    private PetitionManager.PetitionState _state;
    private String _content;
    private List<Say2> _messageLog;
    private int _petitioner;
    private int _responder;

    public Petition(Player petitioner, String petitionText, int petitionType) {
      this._state = PetitionManager.PetitionState.Pending;
      this._messageLog = new ArrayList();
      this._id = PetitionManager.this.getNextId();
      this._type = PetitionManager.PetitionType.values()[petitionType - 1];
      this._content = petitionText;
      this._petitioner = petitioner.getObjectId();
    }

    protected boolean addLogMessage(Say2 cs) {
      return this._messageLog.add(cs);
    }

    protected List<Say2> getLogMessages() {
      return this._messageLog;
    }

    public boolean endPetitionConsultation(PetitionManager.PetitionState endState) {
      this.setState(endState);
      this._endTime = System.currentTimeMillis();
      if (this.getResponder() != null && this.getResponder().isOnline()) {
        if (endState == PetitionManager.PetitionState.Responder_Reject) {
          this.getPetitioner().sendMessage("Your petition was rejected. Please try again later.");
        } else {
          this.getResponder().sendPacket((new SystemMessage(395)).addString(this.getPetitionerName()));
          if (endState == PetitionManager.PetitionState.Petitioner_Cancel) {
            this.getResponder().sendPacket((new SystemMessage(391)).addNumber(this.getId()));
          }
        }
      }

      if (this.isPetitionerOnline()) {
        this.getPetitioner().sendPacket(new SystemMessage(387));
      }

      PetitionManager.this.getCompletedPetitions().put(this.getId(), this);
      return PetitionManager.this.getPendingPetitions().remove(this.getId()) != null;
    }

    public String getContent() {
      return this._content;
    }

    public int getId() {
      return this._id;
    }

    public Player getPetitioner() {
      return World.getPlayer(this._petitioner);
    }

    public boolean isPetitionerOnline() {
      Player pePlayer = World.getPlayer(this._petitioner);
      return pePlayer != null ? pePlayer.isOnline() : false;
    }

    public String getPetitionerName() {
      Player pePlayer = World.getPlayer(this._petitioner);
      return pePlayer != null ? pePlayer.getName() : StringUtils.defaultString(CharacterDAO.getInstance().getNameByObjectId(this._petitioner), "[Unknown]");
    }

    public Player getResponder() {
      return World.getPlayer(this._responder);
    }

    public long getEndTime() {
      return this._endTime;
    }

    public long getSubmitTime() {
      return this._submitTime;
    }

    public PetitionManager.PetitionState getState() {
      return this._state;
    }

    public String getTypeAsString() {
      return this._type.toString().replace("_", " ");
    }

    public void sendPetitionerPacket(L2GameServerPacket responsePacket) {
      Player pePlayer = World.getPlayer(this._petitioner);
      if (pePlayer != null) {
        pePlayer.sendPacket(responsePacket);
      }

    }

    public void sendResponderPacket(L2GameServerPacket responsePacket) {
      if (this.getResponder() != null && this.getResponder().isOnline()) {
        this.getResponder().sendPacket(responsePacket);
      } else {
        this.endPetitionConsultation(PetitionManager.PetitionState.Responder_Missing);
      }
    }

    public void setState(PetitionManager.PetitionState state) {
      this._state = state;
    }

    public void setResponder(Player responder) {
      if (this.getResponder() == null) {
        this._responder = responder.getObjectId();
      }
    }
  }

  public static enum PetitionType {
    Immobility,
    Recovery_Related,
    Bug_Report,
    Quest_Related,
    Bad_User,
    Suggestions,
    Game_Tip,
    Operation_Related,
    Other;

    private PetitionType() {
    }
  }

  public static enum PetitionState {
    Pending,
    Responder_Cancel,
    Responder_Missing,
    Responder_Reject,
    Responder_Complete,
    Petitioner_Cancel,
    Petitioner_Missing,
    In_Process,
    Completed;

    private PetitionState() {
    }
  }
}
