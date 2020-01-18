package org.iacchus.io;

import org.iacchus.audio.Processor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class SpectrumFile {
    public static final int HEADER_LENGTH = 7;
    byte[] bytes;
    String path;
    public SpectrumFile(File f) throws IOException {
        bytes = Files.readAllBytes(f.toPath());
        path = f.toString();
    }
    public SpectrumFile(String path) throws IOException {
        bytes = Files.readAllBytes(new File(path).toPath());
        this.path = path;
    }

    public void save(){
        //ToDo: Method to save SpectrumFile
    }

    public int[] getFrequencies(int time){
        int[] out = new int[getRowLength()];
        for(int i=0; i<out.length; i++){
            out[i] =getFrequency(time, i);
        }
        return out;
    }
    public int getFrequency(int time, int freqIndex){
        int index = (getRowLength()*4)*time+HEADER_LENGTH+freqIndex;
        String binaryString = "";
        for(int i=index; i<index+4; i++){
            binaryString = Processor.byteToString(bytes[i]) + binaryString;
        }
        return Integer.parseInt(binaryString, 2);
    }
    public int getRowLength(){
        String binaryString = "";
        for(int i=4; i<8; i++){
            binaryString = Processor.byteToString(bytes[i]) + binaryString;
        }
        return Integer.parseInt(binaryString, 2);
    }
}
