package org.iacchus.prototyping;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class Prototype1 {
    public static void runPrototype1() {
        System.out.println("Running now");
        double[] testData2 = new double[1024];
        for(int i=0; i<1024; i++){
            testData2[i] = Math.sin(i*2);
        }
        /*       double[] testdata = {23, 43, 43, 21, 22, 11, 3, 54, 22, 85, 12l, 8, 32, 14, 54, 43};*/
        for(int i = 0; makePeriodogram(testData2).length > i; i++) {
            System.out.println(makePeriodogram(testData2)[i]);

        }
    }
    private static double[] makePeriodogram(double[] data) {
        // Need to insert checks about power of 2, evenness, etc.
        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
        Complex[] transformed = fft.transform(data, TransformType.FORWARD);
        int upperLimit = (int) (transformed.length / 2.0) - 1;
        double[] periodogram = new double[upperLimit];
        for (int i = 0; i < upperLimit; i++) {
            periodogram[i] = Math.pow(transformed[i + 1].abs(), 2);
        }
        return periodogram;
    }
}
