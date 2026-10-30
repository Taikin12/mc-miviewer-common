package dev.taikin.miviewer.common;

/**
 * Eigendecomposition of a symmetric 3x3 displacement tensor (Uij, Å²).
 *
 * <p>Input order is U11, U22, U33, U12, U13, U23. The returned axes are
 * sqrt(eigenvalues) in Å sorted from longest to shortest; rotation columns are
 * the corresponding principal axes.</p>
 */
public final class ThermalEllipsoid {

    public record Ellipsoid(double[] axes, double[][] rotation) {
    }

    private static final double EPSILON = 1.0e-14;

    private ThermalEllipsoid() {
    }

    public static Ellipsoid decompose(double u11, double u22, double u33,
            double u12, double u13, double u23) {
        double[][] a = {
            {u11, u12, u13},
            {u12, u22, u23},
            {u13, u23, u33}
        };
        double[][] v = {
            {1.0, 0.0, 0.0},
            {0.0, 1.0, 0.0},
            {0.0, 0.0, 1.0}
        };

        for (int iteration = 0; iteration < 64; iteration++) {
            int p = 0;
            int q = 1;
            double largest = Math.abs(a[0][1]);
            if (Math.abs(a[0][2]) > largest) {
                largest = Math.abs(a[0][2]);
                p = 0;
                q = 2;
            }
            if (Math.abs(a[1][2]) > largest) {
                largest = Math.abs(a[1][2]);
                p = 1;
                q = 2;
            }
            if (largest < EPSILON) {
                break;
            }
            double angle = 0.5 * Math.atan2(2.0 * a[p][q], a[q][q] - a[p][p]);
            double cosine = Math.cos(angle);
            double sine = Math.sin(angle);

            double app = a[p][p];
            double aqq = a[q][q];
            double apq = a[p][q];
            a[p][p] = cosine * cosine * app - 2.0 * sine * cosine * apq + sine * sine * aqq;
            a[q][q] = sine * sine * app + 2.0 * sine * cosine * apq + cosine * cosine * aqq;
            a[p][q] = 0.0;
            a[q][p] = 0.0;

            for (int i = 0; i < 3; i++) {
                if (i != p && i != q) {
                    double aip = a[i][p];
                    double aiq = a[i][q];
                    a[i][p] = cosine * aip - sine * aiq;
                    a[p][i] = a[i][p];
                    a[i][q] = sine * aip + cosine * aiq;
                    a[q][i] = a[i][q];
                }
            }
            for (int i = 0; i < 3; i++) {
                double vip = v[i][p];
                double viq = v[i][q];
                v[i][p] = cosine * vip - sine * viq;
                v[i][q] = sine * vip + cosine * viq;
            }
        }

        Integer[] order = {0, 1, 2};
        java.util.Arrays.sort(order, (first, second) ->
                Double.compare(a[second][second], a[first][first]));
        double[] axes = new double[3];
        double[][] rotation = new double[3][3];
        for (int column = 0; column < 3; column++) {
            int source = order[column];
            axes[column] = Math.sqrt(Math.max(0.0, a[source][source]));
            for (int row = 0; row < 3; row++) {
                rotation[row][column] = v[row][source];
            }
        }
        if (determinant(rotation) < 0.0) {
            for (int row = 0; row < 3; row++) {
                rotation[row][2] = -rotation[row][2];
            }
        }
        return new Ellipsoid(axes, rotation);
    }

    private static double determinant(double[][] m) {
        return m[0][0] * (m[1][1] * m[2][2] - m[1][2] * m[2][1])
                - m[0][1] * (m[1][0] * m[2][2] - m[1][2] * m[2][0])
                + m[0][2] * (m[1][0] * m[2][1] - m[1][1] * m[2][0]);
    }
}
