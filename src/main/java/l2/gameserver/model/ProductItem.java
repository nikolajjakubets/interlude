//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model;

import java.util.ArrayList;
import java.util.Calendar;

public class ProductItem {
  public static final long NOT_LIMITED_START_TIME = 315547200000L;
  public static final long NOT_LIMITED_END_TIME = 2127445200000L;
  public static final int NOT_LIMITED_START_HOUR = 0;
  public static final int NOT_LIMITED_END_HOUR = 23;
  public static final int NOT_LIMITED_START_MIN = 0;
  public static final int NOT_LIMITED_END_MIN = 59;
  private final int _productId;
  private final int _category;
  private final int _points;
  private final int _tabId;
  private final long _startTimeSale;
  private final long _endTimeSale;
  private final int _startHour;
  private final int _endHour;
  private final int _startMin;
  private final int _endMin;
  private ArrayList<ProductItemComponent> _components;

  public ProductItem(int productId, int category, int points, int tabId, long startTimeSale, long endTimeSale) {
    this._productId = productId;
    this._category = category;
    this._points = points;
    this._tabId = tabId;
    Calendar calendar;
    if (startTimeSale > 0L) {
      calendar = Calendar.getInstance();
      calendar.setTimeInMillis(startTimeSale);
      this._startTimeSale = startTimeSale;
      this._startHour = calendar.get(11);
      this._startMin = calendar.get(12);
    } else {
      this._startTimeSale = 315547200000L;
      this._startHour = 0;
      this._startMin = 0;
    }

    if (endTimeSale > 0L) {
      calendar = Calendar.getInstance();
      calendar.setTimeInMillis(endTimeSale);
      this._endTimeSale = endTimeSale;
      this._endHour = calendar.get(11);
      this._endMin = calendar.get(12);
    } else {
      this._endTimeSale = 2127445200000L;
      this._endHour = 23;
      this._endMin = 59;
    }

  }

  public void setComponents(ArrayList<ProductItemComponent> a) {
    this._components = a;
  }

  public ArrayList<ProductItemComponent> getComponents() {
    if (this._components == null) {
      this._components = new ArrayList<>();
    }

    return this._components;
  }

  public int getProductId() {
    return this._productId;
  }

  public int getCategory() {
    return this._category;
  }

  public int getPoints() {
    return this._points;
  }

  public int getTabId() {
    return this._tabId;
  }

  public long getStartTimeSale() {
    return this._startTimeSale;
  }

  public int getStartHour() {
    return this._startHour;
  }

  public int getStartMin() {
    return this._startMin;
  }

  public long getEndTimeSale() {
    return this._endTimeSale;
  }

  public int getEndHour() {
    return this._endHour;
  }

  public int getEndMin() {
    return this._endMin;
  }
}
