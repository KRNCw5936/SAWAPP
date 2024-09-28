package com.mycompany.sawapp;

public class SAWMethod {
    private double[][] decisionMatrix;
    private double[] weights;
    private boolean[] isBenefit;

    public SAWMethod(double[][] decisionMatrix, double[] weights, boolean[] isBenefit) {
        this.decisionMatrix = decisionMatrix;
        this.weights = weights;
        this.isBenefit = isBenefit;
    }

    public double[] calculateSAW() {
        double[] normalizedMatrix = normalizeMatrix();
        double[] result = new double[decisionMatrix.length];

        for (int i = 0; i < decisionMatrix.length; i++) {
            result[i] = 0;
            for (int j = 0; j < decisionMatrix[0].length; j++) {
                result[i] += normalizedMatrix[i * decisionMatrix[0].length + j] * weights[j];
            }
        }
        return result;
    }

    private double[] normalizeMatrix() {
        double[] normalizedMatrix = new double[decisionMatrix.length * decisionMatrix[0].length];

        // Proses normalisasi
        for (int j = 0; j < decisionMatrix[0].length; j++) {
            double max = Double.MIN_VALUE;
            double min = Double.MAX_VALUE;

            // Mencari nilai maksimum dan minimum untuk setiap kriteria
            for (int i = 0; i < decisionMatrix.length; i++) {
                max = Math.max(max, decisionMatrix[i][j]);
                min = Math.min(min, decisionMatrix[i][j]);
            }

            // Normalisasi berdasarkan tipe kriteria (benefit atau cost)
            for (int i = 0; i < decisionMatrix.length; i++) {
                if (isBenefit[j]) {
                    // Jika kriteria benefit
                    normalizedMatrix[i * decisionMatrix[0].length + j] = decisionMatrix[i][j] / max;
                } else {
                    // Jika kriteria cost
                    normalizedMatrix[i * decisionMatrix[0].length + j] = min / decisionMatrix[i][j];
                }
            }
        }
        return normalizedMatrix;
    }
}