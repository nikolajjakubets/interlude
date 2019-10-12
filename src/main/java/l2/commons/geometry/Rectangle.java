package l2.commons.geometry;

public class Rectangle extends AbstractShape {
    public Rectangle(int x1, int y1, int x2, int y2) {
        this.min.x = Math.min(x1, x2);
        this.min.y = Math.min(y1, y2);
        this.max.x = Math.max(x1, x2);
        this.max.y = Math.max(y1, y2);
    }

    public Rectangle setZmax(int z) {
        this.max.z = z;
        return this;
    }

    public Rectangle setZmin(int z) {
        this.min.z = z;
        return this;
    }

    public boolean isInside(int x, int y) {
        return x >= this.min.x && x <= this.max.x && y >= this.min.y && y <= this.max.y;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(this.min).append(", ").append(this.max);
        sb.append("]");
        return sb.toString();
    }
}
