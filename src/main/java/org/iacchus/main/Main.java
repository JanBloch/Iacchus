package org.iacchus.main;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.iacchus.audio.Processor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDateTime;


public class Main {
    public static int maxFreqPower = 8;
    public static int maxFreq = (int)Math.pow(2, maxFreqPower);
    public static void main(String[] args) {

        Processor p = new Processor();
        try {


            double[] audio = p.getAudio("C:\\Windows\\media\\ir_begin.wav");
            int length;
            int height = 0;
            for(int j=4; j<audio.length; j=(j>maxFreq*2)?j+maxFreq*2:j*2) {
             height++;
            }
            double[][] outp = new double[height][maxFreq];

            BufferedImage outImage = new BufferedImage(height, maxFreq,BufferedImage.TYPE_INT_RGB);

            int counter = 0;
            for(int j=4; j<audio.length; j=(j>maxFreq*2)?j+maxFreq*2:j*2) {

                if(j>maxFreq*2){
                    length = maxFreq*2;
                }else{
                    length = j;
                }
                double[] out = pad(realFFT(part(audio,j-length, j)), maxFreq);
                outp[counter] = out;
                counter++;
                /*for(int i=0; i<out.length-1; i++){

                    System.out.print(out[i] + "\t");

                }
                System.out.println(out[out.length-1]);*/

            }
            double max = 0;
            for(int i=0; i<outp.length; i++){
                for(int j=0; j<outp[i].length; j++){
                    outp[i][j] = Math.log(outp[i][j]);
                    if(outp[i][j]>max) max=(int)outp[i][j];
                }
            }
            for(int i=0; i<outp.length; i++){
                for(int j=0; j<outp[i].length; j++){
                    try {
                        int red = (int) (outp[i][j] / max * 255);
                        int green = (int) (outp[i][j] / max * 255);
                        ;
                        int blue = (int) (outp[i][j] / max * 255);
                        Color c = new Color(red, green, blue);
                        outImage.setRGB(i, j, c.getRGB());
                    }catch(Exception ex){
                        System.out.println(outp[i][j]);
                    }

                }
            }
            ImageIO.write(outImage, "jpg", new File("test.jpg"));
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private static double[] pad(double[] arr, int length){
        if(arr.length>length){
            return arr;
        }else{
            double[] out = new double[length];
            for(int i=0; i<arr.length; i++){
                out[i] = arr[i];
            }
            for(int i=arr.length-1;i<length;i++){
                out[i] = 0;
            }
            return out;
        }
    }
    private static double[] part(double[] arr, int start, int end) throws Exception {

        double[] out = new double[end-start];
        for(int i=start; i<end; i++){
            out[i-start] = arr[i];
        }
        return out;
    }
    private static double[] realFFT(double[] data) {
        // Need to insert checks about power of 2, evenness, etc.
        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
        Complex[] transformed = fft.transform(data, TransformType.FORWARD);
        int upperLimit = (int) (transformed.length / 2.0) - 1;
        double[] out = new double[upperLimit];
        for (int i = 0; i < upperLimit; i++) {
            out[i] = Math.pow(transformed[i + 1].abs(), 2);
        }
        return out;
    }
}
