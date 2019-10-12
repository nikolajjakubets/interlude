package l2.commons.geometry;

public class GeometryUtils {
    private GeometryUtils() {
    }

    public static boolean checkIfLinesIntersects(Point2D a, Point2D b, Point2D c, Point2D d) {
        return checkIfLinesIntersects(a, b, c, d, (Point2D)null);
    }

    public static boolean checkIfLinesIntersects(Point2D a, Point2D b, Point2D c, Point2D d, Point2D r) {
        if (a.x == b.x && a.y == b.y || c.x == d.x && c.y == d.y) {
            return false;
        } else {
            double Bx = (double)(b.x - a.x);
            double By = (double)(b.y - a.y);
            double Cx = (double)(c.x - a.x);
            double Cy = (double)(c.y - a.y);
            double Dx = (double)(d.x - a.x);
            double Dy = (double)(d.y - a.y);
            double distAB = Math.sqrt(Bx * Bx + By * By);
            double theCos = Bx / distAB;
            double theSin = By / distAB;
            double newX = Cx * theCos + Cy * theSin;
            Cy = (double)((int)(Cy * theCos - Cx * theSin));
            Cx = newX;
            newX = Dx * theCos + Dy * theSin;
            Dy = (double)((int)(Dy * theCos - Dx * theSin));
            if (Cy == Dy) {
                return false;
            } else {
                double ABpos = newX + (Cx - newX) * Dy / (Dy - Cy);
                if (r != null) {
                    r.x = (int)((double)a.x + ABpos * theCos);
                    r.y = (int)((double)a.y + ABpos * theSin);
                }

                return true;
            }
        }
    }

    public static boolean checkIfLineSegementsIntersects(Point2D a, Point2D b, Point2D c, Point2D d) {
        return checkIfLineSegementsIntersects(a, b, c, d, (Point2D)null);
    }

    public static boolean checkIfLineSegementsIntersects(Point2D a, Point2D b, Point2D c, Point2D d, Point2D r) {
        if ((a.x != b.x || a.y != b.y) && (c.x != d.x || c.y != d.y)) {
            if (a.x == c.x && a.y == c.y || b.x == c.x && b.y == c.y || a.x == d.x && a.y == d.y || b.x == d.x && b.y == d.y) {
                return false;
            } else {
                double Bx = (double)(b.x - a.x);
                double By = (double)(b.y - a.y);
                double Cx = (double)(c.x - a.x);
                double Cy = (double)(c.y - a.y);
                double Dx = (double)(d.x - a.x);
                double Dy = (double)(d.y - a.y);
                double distAB = Math.sqrt(Bx * Bx + By * By);
                double theCos = Bx / distAB;
                double theSin = By / distAB;
                double newX = Cx * theCos + Cy * theSin;
                Cy = (double)((int)(Cy * theCos - Cx * theSin));
                Cx = newX;
                newX = Dx * theCos + Dy * theSin;
                Dy = (double)((int)(Dy * theCos - Dx * theSin));
                if (Cy < 0.0D && Dy < 0.0D || Cy >= 0.0D && Dy >= 0.0D) {
                    return false;
                } else {
                    double ABpos = newX + (Cx - newX) * Dy / (Dy - Cy);
                    if (ABpos >= 0.0D && ABpos <= distAB) {
                        if (r != null) {
                            r.x = (int)((double)a.x + ABpos * theCos);
                            r.y = (int)((double)a.y + ABpos * theSin);
                        }

                        return true;
                    } else {
                        return false;
                    }
                }
            }
        } else {
            return false;
        }
    }
}
