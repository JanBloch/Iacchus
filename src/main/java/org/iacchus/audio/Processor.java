package org.iacchus.audio;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.iacchus.io.SpectrumFile;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;


public class Processor {
    static String lastStatus = "";
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

    public BufferedImage arrayToImage(double[][] array){
        double max = max(array);
        BufferedImage img = new BufferedImage(array[0].length,array.length,BufferedImage.TYPE_INT_RGB);
          for (int i = 0; i < array.length; i++) {
              for (int j = 0; j < array[i].length; j++) {
                  try {
                      int color = (int) (array[i][j] / max * 255);
                      img.setRGB(j, i, (int) Math.pow(color, 3));
                  } catch (Exception ex) {
                      System.out.println(array[i][j]);
                  }


              }
          }
    return img;
    }
    public static double[] pad(double[] arr, int length){
        if(arr.length>=length){
            double[] out = new double[arr.length];
            for(int i=0; i<arr.length; i++){
                out[i] = arr[i];
            }
            return out;
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
    public static double[] part(double[] arr, int start, int end) throws Exception {
        double[] out = new double[end-start];
        for(int i=start; i<end; i++){
            out[i-start] = arr[i];
        }
        return out;
    }

    public static void setStatus(String status){
        if(!status.equals(lastStatus)){
            System.out.print(status + "\r");
            System.out.flush();
            lastStatus = status;
        }
    }
    public static void newStatus(){
        System.out.println();
    }
    public static String repeat(char chr, int amount){
        String out = "";
        for(int i=0; i<amount; i++){
            out += chr;
        }
        return out;
    }
    public static void writeSpectrumFile(double[][] array){
        int h = array.length;
        Thread t = new Thread(){
            @Override
            public void run(){
                try {
                    newStatus();
                    double max = Processor.max(array);
                    SpectrumFile file = SpectrumFile.create("test.spectrum", array[0].length, array.length);
                    file.setMaxValue((int)max);
                    for(int i=0; i<array.length; i++){
                        for(int j=0; j<array[i].length; j++){
                            file.setFrequency(i, j, (int)array[i][j]);
                        }
                        int amount = (int)Math.round((double)i/(double)array.length*100);
                        Processor.setStatus("Preparing to save\t" + repeat('|', amount) + repeat('.', 100-amount) +"\t" + amount + "%");
                    }
                    Processor.newStatus();
                    Processor.setStatus("Saving...");
                    Processor.newStatus();
                    file.save();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        };
        t.start();
    }
    public static double max(double[][] array) {
        double max = 0;
        for(int i=0; i<array.length; i++){
            for(int j=0; j<array[i].length; j++){
                if(array[i][j]>=max) max=array[i][j];
            }
        }
        return max;
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
