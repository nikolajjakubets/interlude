//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.oly;

public enum CompetitionState {
  INIT(0),
  STAND_BY(1),
  PLAYING(2),
  FINISH(0);

  private int _state_id;

  private CompetitionState(int state_id) {
    this._state_id = state_id;
  }

  public int getStateId() {
    return this._state_id;
  }
}
