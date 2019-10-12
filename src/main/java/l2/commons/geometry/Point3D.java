package l2.commons.geometry;

public class Point3D extends Point2D {
    public static final Point3D[] EMPTY_ARRAY = new Point3D[0];
    public int z;

    public Point3D() {
    }

    public Point3D(int x, int y, int z) {
        super(x, y);
        this.z = z;
    }

    public int getZ() {
        return this.z;
    }

    public Point3D clone() {
        return new Point3D(this.x, this.y, this.z);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o == null) {
            return false;
        } else {
            return o.getClass() != this.getClass() ? false : this.equals((Point3D)o);
        }
    }

    public boolean equals(Point3D p) {
        return this.equals(p.x, p.y, p.z);
    }

    public boolean equals(int x, int y, int z) {
        return this.x == x && this.y == y && this.z == z;
    }

    public String toString() {
        return "[x: " + this.x + " y: " + this.y + " z: " + this.z + "]";
    }
}
