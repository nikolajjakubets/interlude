//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class GameObjectArray<E extends GameObject> implements Iterable<E> {
  public final String name;
  public final int resizeStep;
  public final int initCapacity;
  private final List<Integer> freeIndexes;
  private E[] elementData;
  private int size = 0;
  private int real_size = 0;

  public GameObjectArray(String _name, int initialCapacity, int _resizeStep) {
    this.name = _name;
    this.resizeStep = _resizeStep;
    this.initCapacity = initialCapacity;
    if (initialCapacity < 0) {
      throw new IllegalArgumentException("Illegal Capacity (" + this.name + "): " + initialCapacity);
    } else if (this.resizeStep < 1) {
      throw new IllegalArgumentException("Illegal resize step (" + this.name + "): " + this.resizeStep);
    } else {
      this.freeIndexes = new ArrayList<>(this.resizeStep);
      this.elementData = (E[]) new GameObject[initialCapacity];
    }
  }

  public int size() {
    return this.size;
  }

  public int getRealSize() {
    return this.real_size;
  }

  public int capacity() {
    return this.elementData.length;
  }

  public synchronized int add(E e) {
    Integer freeIndex = null;
    if (this.freeIndexes.size() > 0) {
      freeIndex = this.freeIndexes.remove(this.freeIndexes.size() - 1);
    }

    if (freeIndex != null) {
      ++this.real_size;
      this.elementData[freeIndex] = e;
      return freeIndex;
    } else {
      if (this.elementData.length <= this.size) {
        int newCapacity = this.elementData.length + this.resizeStep;
        log.warn("Object array [" + this.name + "] resized: " + this.elementData.length + " -> " + newCapacity);
        this.elementData = Arrays.copyOf(this.elementData, newCapacity);
      }

      this.elementData[this.size++] = e;
      ++this.real_size;
      return this.size - 1;
    }
  }

  public synchronized E remove(int index, int expectedObjId) {
    if (index >= this.size) {
      return null;
    } else {
      E old = this.elementData[index];
      if (old != null && old.getObjectId() == expectedObjId) {
        this.elementData[index] = null;
        --this.real_size;
        if (index == this.size - 1) {
          --this.size;
        } else {
          this.freeIndexes.add(index);
        }

        return old;
      } else {
        return null;
      }
    }
  }

  public E get(int index) {
    return index >= this.size ? null : this.elementData[index];
  }

  public E findByObjectId(int objId) {
    if (objId <= 0) {
      return null;
    } else {
      for (int i = 0; i < this.size; ++i) {
        E o = this.elementData[i];
        if (o != null && o.getObjectId() == objId) {
          return o;
        }
      }

      return null;
    }
  }

  public E findByName(String s) {
    if (s == null) {
      return null;
    } else {
      for (int i = 0; i < this.size; ++i) {
        E o = this.elementData[i];
        if (o != null && s.equalsIgnoreCase(o.getName())) {
          return o;
        }
      }

      return null;
    }
  }

  public List<E> findAllByName(String s) {
    if (s == null) {
      return null;
    } else {
      List<E> result = new ArrayList<>();

      for (int i = 0; i < this.size; ++i) {
        E o = this.elementData[i];
        if (o != null && s.equalsIgnoreCase(o.getName())) {
          result.add(o);
        }
      }

      return result;
    }
  }

  public List<E> getAll() {
    return this.getAll(new ArrayList(this.size));
  }

  public List<E> getAll(List<E> list) {
    for (int i = 0; i < this.size; ++i) {
      E o = this.elementData[i];
      if (o != null) {
        list.add(o);
      }
    }

    return list;
  }

  private int indexOf(E o) {
    if (o == null) {
      return -1;
    } else {
      for (int i = 0; i < this.size; ++i) {
        if (o.equals(this.elementData[i])) {
          return i;
        }
      }

      return -1;
    }
  }

  public boolean contains(E o) {
    return this.indexOf(o) > -1;
  }

  public synchronized void clear() {
    this.elementData = (E[]) new GameObject[0];
    this.size = 0;
    this.real_size = 0;
  }

  public Iterator<E> iterator() {
    return new GameObjectArray.Itr();
  }

  class Itr implements Iterator<E> {
    private int cursor = 0;
    private E _next;

    Itr() {
    }

    public boolean hasNext() {
      while (true) {
        if (this.cursor < GameObjectArray.this.size) {
          if ((this._next = GameObjectArray.this.elementData[this.cursor++]) == null) {
            continue;
          }

          return true;
        }

        return false;
      }
    }

    public E next() {
      E result = this._next;
      this._next = null;
      if (result == null) {
        throw new NoSuchElementException();
      } else {
        return result;
      }
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
}
