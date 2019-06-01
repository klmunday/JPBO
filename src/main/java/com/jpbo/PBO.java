package com.jpbo;

import java.io.*;
import java.nio.file.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class PBO {

    private String filename;
    private String path;
    private PBOProductHeader productHeader;
    private PBOStrings strings;
    private ArrayList<PBOHeader> headers;
    private long dataBlockOffset;

    public PBO(String path, PBOProductHeader productHeader, ArrayList<PBOHeader> headers, long dataBlockOffset) {
        this.filename = path;
        this.path = path;
        this.productHeader = productHeader;
        this.strings = productHeader.getStrings();
        this.headers = headers;
        this.dataBlockOffset = dataBlockOffset;
    }

    @Override
    public String toString() {
        return "PBO(" + this.filename + ")"
                + "\nPath: " + this.path
                + "\nStrings: " + this.strings.size()
                + "\nHeaders: " + this.headers.size()
                + "\n";
    }

    public static PBO read(String filepath) throws Exception {
        try (PBOInputStream pboReader = new PBOInputStream(filepath)) {
            // Read product header (incl strings)
            PBOProductHeader productHeader = PBOProductHeader.read(pboReader);

            // Read headers
            ArrayList<PBOHeader> pboHeaders = new ArrayList<>();
            long dataOffset = 0;
            long headerOffset = pboReader.getPosition();
            for (PBOHeader header = PBOHeader.read(pboReader, dataOffset, headerOffset); !header.isEmpty();
                 header = PBOHeader.read(pboReader, dataOffset, headerOffset)) {
                pboHeaders.add(header);
                dataOffset += header.getDataSize();
                headerOffset = pboReader.getPosition();
            }

            long dataBlockOffset = pboReader.getChannel().position();
            return new PBO(filepath, productHeader, pboHeaders, dataBlockOffset);
        }
    }

    public void backup() throws IOException {
        Path from = Paths.get(this.getPath());
        Path to = Paths.get(this.getPath() + ".bak");
        Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
    }

    public void save() throws NoSuchAlgorithmException, IOException {
        try (PBOAccessFile pboAccessFile = new PBOAccessFile(this)) {
            pboAccessFile.savePBO();
        }
    }

    public static byte[] decompressEntry(byte[] data, PBOHeader entry) {
        long sizeIn = entry.getDataSize() > 0 ? entry.getDataSize() : 0x7fffffff;
        long sizeOut = entry.getOriginalSize();
        int i, j, k, pr;
        byte c;
        int r = 0;
        int pi = 0;
        int po = 0;
        char flags = 0;
        byte[] dataOut = new byte[(int) sizeOut];
        byte[] buf = new byte[0x100F];
        for (int index = 0; index < 0x100F; buf[index] = 0x20, index++)

        while (pi < sizeIn && po < sizeOut) {
            flags >>>= 1;
            if (((flags) & 256) == 0) {
                if (pi >= sizeIn)
                    break;
                c = data[pi++];
                flags = (char) (c | 0xff00);
            }
            if ((flags & 1) != 0) {
                if (pi >= sizeIn || po >= sizeOut)
                    break;
                c = data[pi++];
                dataOut[po++] = c;
                buf[r++] = c;
                r &= 0xfff;  //4095             
            } else {
                if ((pi + 1) >= sizeIn)
                    break;
                i = data[pi++] & 0xff;
                j = data[pi++] & 0xff;
                i |= (j & 0xf0) << 4;
                j = (j & 0x0f) + 2;
                pr = r;
                for (k = 0; k <= j; k++) {
                    c = buf[(pr - i + k) & 0xfff];
                    if (po >= sizeOut)
                        break;
                    dataOut[po++] = c;
                    buf[r++] = c;
                    r &= 0xfff;
                }
            }
        }
        return dataOut;
    }

    public void unpack(String outputDir) throws InterruptedException {
        UnpackerThread thread = new UnpackerThread(this.path, outputDir, this.headers, this.dataBlockOffset);
        thread.start();
        thread.join();
    }

    public void unpack(String outputDir, int threadCount) throws InterruptedException {
        if (threadCount > this.headers.size()) {
            System.err.println("Thread count greater than header count. Defaulting to header count");
            threadCount = this.headers.size();
        } else if (threadCount < 1) {
            System.err.println("Thread count needs to be greater than 0. Defaulting to 1");
            threadCount = 1;
        }

        // Create copy of headers and sort them by size for load balancing between threads
        ArrayList<PBOHeader> headersCopy = new ArrayList<>(this.headers.size());
        headersCopy.addAll(this.headers);
        Collections.sort(headersCopy);

        ArrayList<UnpackerThread> threads = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            ArrayList<PBOHeader> threadHeaders = new ArrayList<>();

            for (int j = i; j < this.headers.size(); j += threadCount)
                threadHeaders.add(headersCopy.get(j));

            UnpackerThread thread = new UnpackerThread(this.path, outputDir, threadHeaders, this.dataBlockOffset);
            threads.add(thread);
            thread.start();
        }

        for (UnpackerThread thread : threads)
            thread.join();
    }

    // TODO: remake to use PBOInputStream?
    public static Boolean validPBOFile(String filepath) throws IOException {
        byte[] magic;
        try (RandomAccessFile pboFile = new RandomAccessFile(filepath, "r")) {
            pboFile.seek(1);
            magic = new byte[4];
            pboFile.read(magic);
        }
        boolean fileIsPBO = Arrays.equals(magic, new byte[]{115, 114, 101, 86});  // check magic number
        return fileIsPBO && filepath.endsWith(".pbo");
    }

    public String getPath() {
        return this.path;
    }

    public String getFilename() {
        return this.path.substring(0, this.path.lastIndexOf("."));
    }

    public PBOProductHeader getProductHeader() {
        return this.productHeader;
    }

    public PBOStrings getStrings() {
        return this.strings;
    }

    public ArrayList<PBOHeader> getHeaders() {
        return this.headers;
    }

    public long getDataBlockOffset() {
        return this.dataBlockOffset;
    }
}
