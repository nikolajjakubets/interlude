package l2.commons.geometry;

public class Circle extends AbstractShape {
    protected final Point2D c;
    protected final int r;

    public Circle(Point2D center, int radius) {
        this.c = center;
        this.r = radius;
        this.min.x = this.c.x - this.r;
        this.max.x = this.c.x + this.r;
        this.min.y = this.c.y - this.r;
        this.max.y = this.c.y + this.r;
    }

    public Circle(int x, int y, int radius) {
        this(new Point2D(x, y), radius);
    }

    public Circle setZmax(int z) {
        this.max.z = z;
        return this;
    }

    public Circle setZmin(int z) {
        this.min.z = z;
        return this;
    }

    public boolean isInside(int x, int y) {
        return (x - this.c.x) * (this.c.x - x) + (y - this.c.y) * (this.c.y - y) <= this.r * this.r;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(this.c).append("{ radius: ").append(this.r).append("}");
        sb.append("]");
        return sb.toString();
    }
}
