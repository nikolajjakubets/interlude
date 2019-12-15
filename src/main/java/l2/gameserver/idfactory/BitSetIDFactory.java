//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.idfactory;

import l2.commons.math.PrimeFinder;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import lombok.extern.slf4j.Slf4j;

import java.util.BitSet;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class BitSetIDFactory extends IdFactory {
  private BitSet freeIds;
  private AtomicInteger freeIdCount;
  private AtomicInteger nextFreeId;

  protected BitSetIDFactory() {
    this.initialize();
    ThreadPoolManager.getInstance().scheduleAtFixedRate(new BitSetIDFactory.BitSetCapacityCheck(), 30000L, 30000L);
  }

  private void initialize() {
    try {
      this.freeIds = new BitSet(PrimeFinder.nextPrime(100000));
      this.freeIds.clear();
      this.freeIdCount = new AtomicInteger(1879048191);
      int[] var1 = this.extractUsedObjectIDTable();

      for (int usedObjectId : var1) {
        int objectID = usedObjectId - 268435456;
        if (objectID < 0) {
          log.warn("Object ID " + usedObjectId + " in DB is less than minimum ID of " + 268435456);
        } else {
          this.freeIds.set(usedObjectId - 268435456);
          this.freeIdCount.decrementAndGet();
        }
      }

      this.nextFreeId = new AtomicInteger(this.freeIds.nextClearBit(0));
      this.initialized = true;
      log.info("IdFactory: " + this.freeIds.size() + " id's available.");
    } catch (Exception var6) {
      this.initialized = false;
      log.error("BitSet ID Factory could not be initialized correctly!", var6);
    }

  }

  public synchronized void releaseId(int objectID) {
    if (objectID - 268435456 > -1) {
      this.freeIds.clear(objectID - 268435456);
      this.freeIdCount.incrementAndGet();
      super.releaseId(objectID);
    } else {
      log.warn("BitSet ID Factory: release objectID " + objectID + " failed (< " + 268435456 + ")");
    }

  }

  public synchronized int getNextId() {
    int newID = this.nextFreeId.get();
    this.freeIds.set(newID);
    this.freeIdCount.decrementAndGet();
    int nextFree = this.freeIds.nextClearBit(newID);
    if (nextFree < 0) {
      nextFree = this.freeIds.nextClearBit(0);
    }

    if (nextFree < 0) {
      if (this.freeIds.size() >= 1879048191) {
        throw new NullPointerException("Ran out of valid Id's.");
      }

      this.increaseBitSetCapacity();
    }

    this.nextFreeId.set(nextFree);
    return newID + 268435456;
  }

  public synchronized int size() {
    return this.freeIdCount.get();
  }

  protected synchronized int usedIdCount() {
    return this.size() - 268435456;
  }

  protected synchronized boolean reachingBitSetCapacity() {
    return PrimeFinder.nextPrime(this.usedIdCount() * 11 / 10) > this.freeIds.size();
  }

  protected synchronized void increaseBitSetCapacity() {
    BitSet newBitSet = new BitSet(PrimeFinder.nextPrime(this.usedIdCount() * 11 / 10));
    newBitSet.or(this.freeIds);
    this.freeIds = newBitSet;
  }

  public class BitSetCapacityCheck extends RunnableImpl {
    public BitSetCapacityCheck() {
    }

    public void runImpl() throws Exception {
      if (BitSetIDFactory.this.reachingBitSetCapacity()) {
        BitSetIDFactory.this.increaseBitSetCapacity();
      }

    }
  }
}
