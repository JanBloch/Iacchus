package org.iacchus.audio;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;


public class Processor {
    public double[] getAudio(String fileName) throws IOException {
        byte[] bytes = Files.readAllBytes(new File(fileName).toPath());
        int bitsPerSample = ((bytes[33] & 0xff) << 8) | (bytes[34] & 0xff);
        double[] audio = new double[(bytes.length-44)/(2*bitsPerSample)*8+1];
        for(int i=44; i<bytes.length; i+=bitsPerSample/4){
            String bits = "";
            for(int j=0; j<bitsPerSample/8; j++) {
                byte _byte = bytes[i  + j];
                bits = byteToString(_byte) + bits;
            }

                audio[(i-44) / bitsPerSample * 4] = Integer.parseInt(bits, 2);

        }
        return audio;
    }

    public static String byteToString(byte b) {
        byte[] masks = { -128, 64, 32, 16, 8, 4, 2, 1 };
        StringBuilder builder = new StringBuilder();
        for (byte m : masks) {
            if ((b & m) == m) {
                builder.append('1');
            } else {
                builder.append('0');
            }
        }
        return builder.toString();
    }
    public static double[] realFFT(double[] data) {
        // Need to insert checks about power of 2, evenness, etc.
        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
        Complex[] transformed = fft.transform(data, TransformType.FORWARD);
        int upperLimit = (int) (transformed.length/2.0) - 1;
        double[] out = new double[upperLimit];
        for (int i = 0; i < upperLimit; i++) {
            out[i] = Math.pow(transformed[i + 1].abs(), 1);
        }
        return out;
    }

}
