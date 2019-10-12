package l2.commons.geometry;

import java.util.Arrays;
import java.util.List;
import l2.commons.lang.ArrayUtils;

public class Polygon extends AbstractShape {
    protected Point2D[] points;

    public Polygon() {
        this.points = Point2D.EMPTY_ARRAY;
    }

    public Polygon add(int x, int y) {
        this.add(new Point2D(x, y));
        return this;
    }

    public Polygon add(Point2D p) {
        if (this.points.length == 0) {
            this.min.y = p.y;
            this.min.x = p.x;
            this.max.x = p.x;
            this.max.y = p.y;
        } else {
            this.min.y = Math.min(this.min.y, p.y);
            this.min.x = Math.min(this.min.x, p.x);
            this.max.x = Math.max(this.max.x, p.x);
            this.max.y = Math.max(this.max.y, p.y);
        }

        this.points = (Point2D[])ArrayUtils.add(this.points, p);
        return this;
    }

    public List<Point2D> getPoints() {
        return Arrays.asList(this.points);
    }

    public Polygon setZmax(int z) {
        this.max.z = z;
        return this;
    }

    public Polygon setZmin(int z) {
        this.min.z = z;
        return this;
    }

    public boolean isInside(int x, int y) {
        if (x >= this.min.x && x <= this.max.x && y >= this.min.y && y <= this.max.y) {
            int hits = 0;
            int npoints = this.points.length;
            Point2D last = this.points[npoints - 1];

            for(int i = 0; i < npoints; ++i) {
                Point2D cur = this.points[i];
                if (cur.y != last.y) {
                    label82: {
                        int leftx;
                        if (cur.x < last.x) {
                            if (x >= last.x) {
                                break label82;
                            }

                            leftx = cur.x;
                        } else {
                            if (x >= cur.x) {
                                break label82;
                            }

                            leftx = last.x;
                        }

                        double test1;
                        double test2;
                        if (cur.y < last.y) {
                            if (y < cur.y || y >= last.y) {
                                break label82;
                            }

                            if (x < leftx) {
                                ++hits;
                                break label82;
                            }

                            test1 = (double)(x - cur.x);
                            test2 = (double)(y - cur.y);
                        } else {
                            if (y < last.y || y >= cur.y) {
                                break label82;
                            }

                            if (x < leftx) {
                                ++hits;
                                break label82;
                            }

                            test1 = (double)(x - last.x);
                            test2 = (double)(y - last.y);
                        }

                        if (test1 < test2 / (double)(last.y - cur.y) * (double)(last.x - cur.x)) {
                            ++hits;
                        }
                    }
                }

                last = cur;
            }

            return (hits & 1) != 0;
        } else {
            return false;
        }
    }

    public boolean validate() {
        if (this.points.length < 3) {
            return false;
        } else {
            if (this.points.length > 3) {
                for(int i = 1; i < this.points.length; ++i) {
                    int ii = i + 1 < this.points.length ? i + 1 : 0;

                    for(int n = i; n < this.points.length; ++n) {
                        if (Math.abs(n - i) > 1) {
                            int nn = n + 1 < this.points.length ? n + 1 : 0;
                            if (GeometryUtils.checkIfLineSegementsIntersects(this.points[i], this.points[ii], this.points[n], this.points[nn])) {
                                return false;
                            }
                        }
                    }
                }
            }

            return true;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for(int i = 0; i < this.points.length; ++i) {
            sb.append(this.points[i]);
            if (i < this.points.length - 1) {
                sb.append(",");
            }
        }

        sb.append(";[").append(this.getZmin()).append("-").append(this.getZmax()).append("]");
        sb.append("]");
        return sb.toString();
    }
}
