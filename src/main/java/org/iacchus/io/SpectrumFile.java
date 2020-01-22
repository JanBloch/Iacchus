package org.iacchus.io;

import org.iacchus.audio.Processor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;

public class SpectrumFile {
    public static final int HEADER_LENGTH = 8;
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

    public static SpectrumFile create(String fileName, int rowLength, int timeCount) throws IOException {
        Files.createFile(new File(fileName).toPath() );
        SpectrumFile f = new SpectrumFile(fileName);
        f.bytes = new byte[HEADER_LENGTH+rowLength*4*timeCount];
        return f;
    }
    public void save() throws IOException {
        FileOutputStream fos = new FileOutputStream(path);
        fos.write(bytes);
        fos.close();
    }

    public void setFrequency(int time, int freqIndex, int value){
        int index = (getRowLength()*4)*time+HEADER_LENGTH+(freqIndex*4);
        byte[] valueArr = ByteBuffer.allocate(4).putInt(value).array();
        for(int i=0; i<4; i++){
            bytes[i+index] = valueArr[i];
        }
    }
    public void setRowLength(int length){
        byte[] values = ByteBuffer.allocate(4).putInt(length).array();
        for(int i=0;i<4; i++){
            bytes[i+4] = values[i];
        }
    }
    public int[] getFrequencies(int time){
        int[] out = new int[getRowLength()];
        for(int i=0; i<out.length; i++){
            out[i] =getFrequency(time, i);
        }
        return out;
    }
    public void setMaxValue(int value){
        byte[] values = ByteBuffer.allocate(4).putInt(value).array();
        for(int i=0; i<4; i++){
            bytes[i] = values[i];
        }
    }
    public int getFrequency(int time, int freqIndex){
        int index = (getRowLength()*4)*time+HEADER_LENGTH+(freqIndex*4);
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
