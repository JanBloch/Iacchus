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
                double[] out = Processor.realFFT(Processor.part(audio,j-length, j));
                double[] out_ = Processor.pad(out, maxFreq);
                outp[counter] = out_;
                counter++;
            }
            audio = null;
            System.gc();
            Processor.writeSpectrumFile(outp);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }






}
