//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.quest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuestRates {
  private static final Logger LOG = LoggerFactory.getLogger(QuestRates.class);
  private final int _questId;
  private double _dropRate;
  private double _rewardRate;
  private double _expRate;
  private double _spRate;

  public QuestRates(int questId) {
    this._questId = questId;
    this._dropRate = 1.0D;
    this._rewardRate = 1.0D;
    this._expRate = 1.0D;
    this._spRate = 1.0D;
  }

  public void updateParam(String paramName, String paramValue) {
    if (!paramName.equalsIgnoreCase("Drop") && !paramName.equalsIgnoreCase("DropRate")) {
      if (!paramName.equalsIgnoreCase("Reward") && !paramName.equalsIgnoreCase("RewardRate")) {
        if (!paramName.equalsIgnoreCase("Exp") && !paramName.equalsIgnoreCase("ExpRate")) {
          if (!paramName.equalsIgnoreCase("Sp") && !paramName.equalsIgnoreCase("SpRate")) {
            throw new IllegalArgumentException("Unknown param \"" + paramName + "\"");
          }

          this.setExpRate(Double.parseDouble(paramValue));
        } else {
          this.setExpRate(Double.parseDouble(paramValue));
        }
      } else {
        this.setRewardRate(Double.parseDouble(paramValue));
      }
    } else {
      this.setDropRate(Double.parseDouble(paramValue));
    }

  }

  public double getDropRate() {
    return this._dropRate;
  }

  public void setDropRate(double dropRate) {
    this._dropRate = dropRate;
  }

  public double getRewardRate() {
    return this._rewardRate;
  }

  public void setRewardRate(double rewardRate) {
    this._rewardRate = rewardRate;
  }

  public double getExpRate() {
    return this._expRate;
  }

  public void setExpRate(double expRate) {
    this._expRate = expRate;
  }

  public double getSpRate() {
    return this._spRate;
  }

  public void setSpRate(double spRate) {
    this._spRate = spRate;
  }
}
