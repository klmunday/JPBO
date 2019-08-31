package com.jpbo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class UnpackerThread extends Thread {

    private String path;
    private String outputDir;
    private ArrayList<PBOHeader> headers;
    private long dataBlockOffset;

    public UnpackerThread(String path, String outputDir, ArrayList<PBOHeader> headers, long dataBlockOffset) {
        this.path = path;
        this.outputDir = outputDir;
        this.headers = headers;
        this.dataBlockOffset = dataBlockOffset;
    }

    @Override
    public void run() {
        File pboDirectory = new File(this.outputDir);

        for (PBOHeader header : this.headers) {
            System.out.println("Unpacking " + header.getPath());

            try (PBOInputStream pboReader = new PBOInputStream(this.path)) {
                byte[] dataBuffer = new byte[(int) header.getDataSize()];

                pboReader.skip(this.dataBlockOffset + header.getDataOffset());
                pboReader.read(dataBuffer, 0, (int) header.getDataSize());

                if (header.getPackingMethod().equals(PackingMethod.COMPRESSED))
                    dataBuffer = PBO.decompressEntry(dataBuffer, header);

                String filename = pboDirectory + File.separator + header.getPath();
                File outFile = new File(filename);
                outFile.getParentFile().mkdirs();

                try (FileOutputStream fileOut = new FileOutputStream(outFile)) {
                    fileOut.write(dataBuffer);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
