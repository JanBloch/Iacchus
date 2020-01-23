package org.iacchus.main;

import org.iacchus.audio.Processor;

import java.util.Arrays;

public class MakeSpectrum {
    public static int maxFreqPower = 13;
    public static int maxFreq = (int)Math.pow(2, maxFreqPower);
    public static void main(String[] args) {
        String outFile;
        String inFile;
        Processor p = new Processor();
        try {
            int indexOut = -1;
            int indexPower = -1;
            indexOut = Arrays.asList(args).indexOf("-o");
            indexPower = Arrays.asList(args).indexOf("-p");
            if(indexPower != -1){
                maxFreqPower = Integer.parseInt(args[indexPower + 1]);
                maxFreq = (int)Math.pow(2, maxFreqPower);
            }
            if(indexOut != -1){
                outFile = args[indexOut+1];
                inFile = args[args.length-1];
            }else{
                inFile = args[args.length-1];
                outFile = inFile + ".spectrum";
            }
            double[] audio = p.getAudio(inFile);
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
                int amount = (int)Math.round((double)j/(double)audio.length*100);
                Processor.setStatus("Creating spectrum " + Processor.repeat('|', amount)+Processor.repeat('.', 100-amount) + "\t" + amount + "%");
            }
            audio = null;
            System.gc();
            Processor.writeSpectrumFile(outp, outFile);
        }catch(Exception ex){
            ex.printStackTrace();
        }

    }
}
