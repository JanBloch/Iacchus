package org.iacchus.audio;

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
        double[] audio = new double[(bytes.length-44)/2];
        for(int i=0; i<(bytes.length-44)/2; i++){
            audio[i] = bytes[i+44];
        }

        return audio;
    }
}
