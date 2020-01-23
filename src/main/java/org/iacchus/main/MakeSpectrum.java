package org.iacchus.main;

import org.iacchus.audio.Processor;

public class MakeSpectrum {
    public static int maxFreqPower = 13;
    public static int maxFreq = (int)Math.pow(2, maxFreqPower);
    public static void main(String[] args) {
        for(int i=0; i<args.length; i++){
            System.out.println(args[i]);
        }
        //if(args.length>0){return;}
        String outFile;
        String inFile;
        Processor p = new Processor();
        try {
            if(args[0].equals("-o")){
                outFile = args[1];
                inFile = args[2];
            }else{
                inFile = args[0];
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
