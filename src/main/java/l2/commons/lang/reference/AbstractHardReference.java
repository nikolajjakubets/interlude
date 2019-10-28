package l2.commons.lang.reference;

public class AbstractHardReference<T> implements HardReference<T> {
    private T reference;

    public AbstractHardReference(T reference) {
        this.reference = reference;
    }

    public T get() {
        return this.reference;
    }

    public void clear() {
        this.reference = null;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o == null) {
            return false;
        } else if (!(o instanceof AbstractHardReference)) {
            return false;
        } else {
          return ((AbstractHardReference) o).get() != null && ((AbstractHardReference) o).get().equals(this.get());
        }
    }
}
