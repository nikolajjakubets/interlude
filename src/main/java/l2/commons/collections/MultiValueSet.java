package l2.commons.collections;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

@Slf4j
public class MultiValueSet<T> extends HashMap<T, Object> {
  private static final long serialVersionUID = 8071544899414292397L;

  public MultiValueSet() {
  }

  public MultiValueSet(int size) {
    super(size);
  }

  public MultiValueSet(MultiValueSet<T> set) {
    super(set);
  }

  public void set(T key, Object value) {
    this.put(key, value);
  }

  public void set(T key, String value) {
    this.put(key, value);
  }

  public void set(T key, boolean value) {
    this.put(key, value ? Boolean.TRUE : Boolean.FALSE);
  }

  public void set(T key, int value) {
    this.put(key, value);
  }

  public void set(T key, int[] value) {
    this.put(key, value);
  }

  public void set(T key, long value) {
    this.put(key, value);
  }

  public void set(T key, double value) {
    this.put(key, value);
  }

  public void set(T key, Enum<?> value) {
    this.put(key, value);
  }

  public void unset(T key) {
    this.remove(key);
  }

  public boolean isSet(T key) {
    return this.get(key) != null;
  }

  public MultiValueSet<T> clone() {
    return new MultiValueSet<>(this);
  }

  public boolean getBool(T key) {
    Object val = this.get(key);
    if (val instanceof Number) {
      return ((Number) val).intValue() != 0;
    } else if (val instanceof String) {
      return Boolean.parseBoolean((String) val);
    } else if (val instanceof Boolean) {
      return (Boolean) val;
    } else {
      throw new IllegalArgumentException("Boolean value required, but found: " + val + "!");
    }
  }

  public boolean getBool(T key, boolean defaultValue) {
    Object val = this.get(key);
    if (val instanceof Number) {
      return ((Number) val).intValue() != 0;
    } else if (val instanceof String) {
      return Boolean.parseBoolean((String) val);
    } else {
      return val instanceof Boolean ? (Boolean) val : defaultValue;
    }
  }

  public int getInteger(T key) {
    Object val = this.get(key);
    if (val instanceof Number) {
      return ((Number) val).intValue();
    } else if (val instanceof String) {
      return Integer.parseInt((String) val);
    } else if (val instanceof Boolean) {
      return (Boolean) val ? 1 : 0;
    } else {
      throw new IllegalArgumentException("Integer value required, but found: " + val + "!");
    }
  }

  public int getInteger(T key, int defaultValue) {
    Object val = this.get(key);
    if (val instanceof Number) {
      return ((Number) val).intValue();
    } else if (val instanceof String) {
      return Integer.parseInt((String) val);
    } else if (val instanceof Boolean) {
      return (Boolean) val ? 1 : 0;
    } else {
      return defaultValue;
    }
  }

  public int[] getIntegerArray(T key) {
    var val = this.get(key);
    if (val instanceof int[]) {
      return (int[]) val;
    } else if (val instanceof Number) {
      return new int[]{((Number) val).intValue()};
    } else if (!(val instanceof String)) {
      throw new IllegalArgumentException("Integer array required, but found: " + val + "!");
    } else {
      String[] vals = ((String) val).split(";");
      int[] result = new int[vals.length];
      int i = 0;

      for (String v : vals) {
        result[i++] = Integer.parseInt(v);
      }

      return result;
    }
  }

  public int[] getIntegerArray(T key, int[] defaultArray) {
    try {
      return this.getIntegerArray(key);
    } catch (IllegalArgumentException e) {
      if ("baseAttributeAttack".equals(key) || "baseAttributeDefence".equals(key)) {
      } else {
        log.warn("getIntegerArray: not found key={}", key);
      }
//      if (!"baseAttributeDefence".equals(key)) {
//        log.warn("getIntegerArray: not found key={}", key);
//      }
      return defaultArray;
    }
  }

  public long getLong(T key) {
    Object val = this.get(key);
    if (val instanceof Number) {
      return ((Number) val).longValue();
    } else if (val instanceof String) {
      return Long.parseLong((String) val);
    } else if (val instanceof Boolean) {
      return (Boolean) val ? 1L : 0L;
    } else {
      throw new IllegalArgumentException("Long value required, but found: " + val + "!");
    }
  }

  public long getLong(T key, long defaultValue) {
    Object val = this.get(key);
    if (val instanceof Number) {
      return ((Number) val).longValue();
    } else if (val instanceof String) {
      return Long.parseLong((String) val);
    } else if (val instanceof Boolean) {
      return (Boolean) val ? 1L : 0L;
    } else {
      return defaultValue;
    }
  }

  public double getDouble(T key) {
    Object val = this.get(key);
    if (val instanceof Number) {
      return ((Number) val).doubleValue();
    } else if (val instanceof String) {
      return Double.parseDouble((String) val);
    } else if (val instanceof Boolean) {
      return (Boolean) val ? 1.0D : 0.0D;
    } else {
      throw new IllegalArgumentException("Double value required, but found: " + val + "!");
    }
  }

  public double getDouble(T key, double defaultValue) {
    Object val = this.get(key);
    if (val instanceof Number) {
      return ((Number) val).doubleValue();
    } else if (val instanceof String) {
      return Double.parseDouble((String) val);
    } else if (val instanceof Boolean) {
      return (Boolean) val ? 1.0D : 0.0D;
    } else {
      return defaultValue;
    }
  }

  public String getString(T key) {
    Object val = this.get(key);
    if (val != null) {
      return String.valueOf(val);
    } else {
      throw new IllegalArgumentException("String value required, but not specified!");
    }
  }

  public String getString(T key, String defaultValue) {
    Object val = this.get(key);
    return val != null ? String.valueOf(val) : defaultValue;
  }

  public Object getObject(T key) {
    return this.get(key);
  }

  public Object getObject(T key, Object defaultValue) {
    Object val = this.get(key);
    return val != null ? val : defaultValue;
  }

  public <E extends Enum<E>> E getEnum(T name, Class<E> enumClass) {
    Object val = this.get(name);
    if (enumClass.isInstance(val)) {
      //TODO: add cast
      return (E) val;
    } else if (val instanceof String) {
      return Enum.valueOf(enumClass, (String) val);
    } else {
      throw new IllegalArgumentException("Enum value of type " + enumClass.getName() + "required, but found: " + val + "!");
    }
  }

  public <E extends Enum<E>> E getEnum(T name, Class<E> enumClass, E defaultValue) {
    Object val = this.get(name);
    if (enumClass.isInstance(val)) {
      //TODO: add cast
      return (E) val;
    } else {
      return val instanceof String ? Enum.valueOf(enumClass, (String) val) : defaultValue;
    }
  }
}
