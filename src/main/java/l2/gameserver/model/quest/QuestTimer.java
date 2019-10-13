//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.quest;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.model.instances.NpcInstance;

public class QuestTimer extends RunnableImpl {
  private String _name;
  private NpcInstance _npc;
  private long _time;
  private QuestState _qs;
  private ScheduledFuture<?> _schedule;

  public QuestTimer(String name, long time, NpcInstance npc) {
    this._name = name;
    this._time = time;
    this._npc = npc;
  }

  void setQuestState(QuestState qs) {
    this._qs = qs;
  }

  QuestState getQuestState() {
    return this._qs;
  }

  void start() {
    this._schedule = ThreadPoolManager.getInstance().schedule(this, this._time);
  }

  public void runImpl() throws Exception {
    QuestState qs = this.getQuestState();
    if (qs != null) {
      qs.removeQuestTimer(this.getName());
      qs.getQuest().notifyEvent(this.getName(), qs, this.getNpc());
    }

  }

  void pause() {
    if (this._schedule != null) {
      this._time = this._schedule.getDelay(TimeUnit.SECONDS);
      this._schedule.cancel(false);
    }

  }

  void stop() {
    if (this._schedule != null) {
      this._schedule.cancel(false);
    }

  }

  public boolean isActive() {
    return this._schedule != null && !this._schedule.isDone();
  }

  public String getName() {
    return this._name;
  }

  public long getTime() {
    return this._time;
  }

  public NpcInstance getNpc() {
    return this._npc;
  }

  public final String toString() {
    return this._name;
  }

  public boolean equals(Object o) {
    if (o == this) {
      return true;
    } else if (o == null) {
      return false;
    } else {
      return o.getClass() != this.getClass() ? false : ((QuestTimer)o).getName().equals(this.getName());
    }
  }
}
