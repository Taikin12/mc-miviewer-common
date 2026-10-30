package dev.taikin.miviewer.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ThermalEllipsoidTest {

    @Test
    void decomposesDiagonalTensor() {
        ThermalEllipsoid.Ellipsoid ellipsoid = ThermalEllipsoid.decompose(
                0.01, 0.04, 0.09, 0.0, 0.0, 0.0);

        assertEquals(0.3, ellipsoid.axes()[0], 1.0e-9);
        assertEquals(0.2, ellipsoid.axes()[1], 1.0e-9);
        assertEquals(0.1, ellipsoid.axes()[2], 1.0e-9);
        assertEquals(1.0, determinant(ellipsoid.rotation()), 1.0e-9);
    }

    @Test
    void decomposesRotatedTensor() {
        double angle = Math.toRadians(35.0);
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        double[][] rotation = {
            {c, -s, 0.0},
            {s, c, 0.0},
            {0.0, 0.0, 1.0}
        };
        double[][] diagonal = {
            {0.01, 0.0, 0.0},
            {0.0, 0.04, 0.0},
            {0.0, 0.0, 0.09}
        };
        double[][] tensor = multiply(multiply(rotation, diagonal), transpose(rotation));

        ThermalEllipsoid.Ellipsoid ellipsoid = ThermalEllipsoid.decompose(
                tensor[0][0], tensor[1][1], tensor[2][2],
                tensor[0][1], tensor[0][2], tensor[1][2]);

        assertEquals(0.3, ellipsoid.axes()[0], 1.0e-8);
        assertEquals(0.2, ellipsoid.axes()[1], 1.0e-8);
        assertEquals(0.1, ellipsoid.axes()[2], 1.0e-8);
        assertEquals(1.0, determinant(ellipsoid.rotation()), 1.0e-8);
    }

    @Test
    void clampsNegativeEigenvalues() {
        ThermalEllipsoid.Ellipsoid ellipsoid = ThermalEllipsoid.decompose(
                0.01, 0.0, 0.0, 0.0, 0.0, 0.0);

        assertTrue(ellipsoid.axes()[0] > 0.0);
        assertEquals(0.0, ellipsoid.axes()[1], 1.0e-9);
        assertEquals(0.0, ellipsoid.axes()[2], 1.0e-9);
    }

    private static double[][] multiply(double[][] first, double[][] second) {
        double[][] result = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    result[i][j] += first[i][k] * second[k][j];
                }
            }
        }
        return result;
    }

    private static double[][] transpose(double[][] matrix) {
        double[][] result = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                result[i][j] = matrix[j][i];
            }
        }
        return result;
    }

    private static double determinant(double[][] m) {
        return m[0][0] * (m[1][1] * m[2][2] - m[1][2] * m[2][1])
                - m[0][1] * (m[1][0] * m[2][2] - m[1][2] * m[2][0])
                + m[0][2] * (m[1][0] * m[2][1] - m[1][1] * m[2][0]);
    }
}
