package org.iacchus.main;

import com.github.matthewbeckler.heatmap.Gradient;
import com.github.matthewbeckler.heatmap.HeatMap;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.iacchus.audio.Processor;
import org.iacchus.io.SpectrumFile;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDateTime;


public class Main {
    public static int maxFreqPower = 13;
    public static int maxFreq = (int)Math.pow(2, maxFreqPower);
    public static void main(String[] args) {

        Processor p = new Processor();
        try {


            double[] audio = p.getAudio("C:/users/janbl/desktop/Audio Files/Pads.wav");
            /*double[] audio = new double[maxFreq*100];
            for(int i=0; i<audio.length; i++){
                audio[i] = Math.sin(i);
            }*/
            int length;
            int height = 0;
            for(int j=4; j<audio.length; j=(j>maxFreq*2)?j+maxFreq/50:j*2) {
             height++;
            }
            double[][] outp = new double[height][maxFreq];


            int counter = 0;
            for(int j=4; j<audio.length; j=(j>maxFreq*2)?j+maxFreq/50:j*2) {

                if(j>maxFreq*2){
                    length = maxFreq*2;
                }else{
                    length = j;
                }
                double[] out = Processor.realFFT(part(audio,j-length, j));
                double[] out_ = pad(out, maxFreq);
                outp[counter] = out_;
                counter++;
                /*for(int i=0; i<out.length-1; i++){

                    System.out.print(out[i] + "\t");

                }
                System.out.println(out[out.length-1]);*/

            }
            audio = null;
            System.gc();
            int h = height;
             Thread t = new Thread(){
              @Override
              public void run(){
                  //BufferedImage outImage = new BufferedImage(maxFreq, h,BufferedImage.TYPE_INT_RGB);
                  try {
                      double max = 0;
                      for(int i=0; i<outp.length; i++){
                          for(int j=0; j<outp[i].length; j++){
                              if(outp[i][j]>=max) max=outp[i][j];
                          }
                      }
                      SpectrumFile file = SpectrumFile.create("test.spectrum", outp[0].length, outp.length);
                      file.setMaxValue((int)max);
                      for(int i=0; i<outp.length; i++){
                          for(int j=0; j<outp[i].length; j++){
                              file.setFrequency(i, j, (int)outp[i][j]);
                          }
                      }
                      file.save();
                      /*
                      for (int i = 0; i < outp.length; i++) {
                          for (int j = 0; j < outp[i].length; j++) {
                              try {
                                  int color = (int) (outp[i][j] / max * 255);
                                  outImage.setRGB(j, i, (int) Math.pow(color, 3));
                              } catch (Exception ex) {
                                  System.out.println(outp[i][j]);
                              }


                          }
                      }

                      ImageIO.write(outImage, "png", new File("test.png"));*/


                    /*HeatMap panel = new HeatMap(outp, true, Gradient.GRADIENT_BLUE_TO_RED);
                    Color[] gradientColors = new Color[]{Color.blue,Color.green,Color.yellow};
                    Color[] customGradient = Gradient.createMultiGradient(gradientColors, (int)max);
                    panel.updateGradient(customGradient);
                    JFrame frame = new JFrame();
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setSize(500, 500);
                    frame.setVisible(true);
                    frame.add(panel);*/
                  }catch(Exception ex){
                        ex.printStackTrace();
                  }
              }
            };
            t.start();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }


    private static double[] pad(double[] arr, int length){
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
    private static double[] part(double[] arr, int start, int end) throws Exception {

        double[] out = new double[end-start];
        for(int i=start; i<end; i++){
            out[i-start] = arr[i];
        }
        return out;
    }


}
