package org.iacchus.io;

import org.iacchus.audio.Processor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class SpectrumFile {
    byte[] bytes;
    public SpectrumFile(File f) throws IOException {
        bytes = Files.readAllBytes(f.toPath());
    }
    public SpectrumFile(String path) throws IOException {
        bytes = Files.readAllBytes(new File(path).toPath());
    }

    public int[] getFrequencies(int time){
        int startIndex = (getRowLength()*4)*time+7;
        int endIndex = (getRowLength()*4)*(time+1)+7-1;
        int[] out = new int[endIndex-startIndex];
        for(int i=startIndex;i<endIndex;i++){
            out[i-startIndex] = bytes[i];
        }
        return out;
    }
    public int getRowLength(){
        String binaryString = "";
        for(int i=4; i<8; i++){
            binaryString = Processor.byteToString(bytes[i]) + binaryString;
        }
        return Integer.parseInt(binaryString, 2);
    }
}
