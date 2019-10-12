package l2.commons.geometry;

public abstract class AbstractShape implements Shape {
    protected final Point3D max = new Point3D();
    protected final Point3D min = new Point3D();

    public AbstractShape() {
    }

    public boolean isInside(int x, int y, int z) {
        return this.min.z <= z && this.max.z >= z && this.isInside(x, y);
    }

    public int getXmax() {
        return this.max.x;
    }

    public int getXmin() {
        return this.min.x;
    }

    public int getYmax() {
        return this.max.y;
    }

    public int getYmin() {
        return this.min.y;
    }

    public AbstractShape setZmax(int z) {
        this.max.z = z;
        return this;
    }

    public AbstractShape setZmin(int z) {
        this.min.z = z;
        return this;
    }

    public int getZmax() {
        return this.max.z;
    }

    public int getZmin() {
        return this.min.z;
    }
}
