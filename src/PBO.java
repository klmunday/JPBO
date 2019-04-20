import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class PBO {

    private String filename;
    private String path;
    private ArrayList<String> strings;
    private ArrayList<Header> headers;
    private long dataBlockOffset;

    public PBO(String path, ArrayList<String> strings, ArrayList<Header> headers, long dataBlockOffset) {
        this.filename = path;
        this.path = path;
        this.strings = strings;
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

    public static PBO read(String filepath) throws IOException {
        try (PBOInputStream pboReader = new PBOInputStream(filepath, 21)) {
            // Read strings
            ArrayList<String> pboStrings = new ArrayList<>();
            for (String str = pboReader.readString(); !str.isEmpty(); str = pboReader.readString())
                pboStrings.add(str);

            // Read headers
            ArrayList<Header> pboHeaders = new ArrayList<>();
            long offset = 0;
            for (Header header = Header.read(pboReader, offset); !header.isEmpty(); header = Header.read(pboReader, offset)) {
                pboHeaders.add(header);
                offset += header.getDataSize();
            }

            long dataBlockOffset = pboReader.getChannel().position();
            return new PBO(filepath, pboStrings, pboHeaders, dataBlockOffset);
        }

    }

    public void unpack(int threadCount) throws InterruptedException {
        if (threadCount > this.headers.size()) {
            System.err.println("Thread count greater than header count. Defaulting to header count");
            threadCount = this.headers.size();
        } else if (threadCount < 1) {
            System.err.println("Thread count needs to be greater than 0. Defaulting to 1");
            threadCount = 1;
        }

        // Create copy of headers and sort them by size for load balancing between threads
        ArrayList<Header> headersCopy = new ArrayList<>(this.headers.size());
        headersCopy.addAll(this.headers);
        Collections.sort(headersCopy);

        ArrayList<UnpackerThread> threads = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            ArrayList<Header> threadHeaders = new ArrayList<>();

            for (int j = i; j < this.headers.size(); j += threadCount)
                threadHeaders.add(headersCopy.get(j));

            UnpackerThread thread = new UnpackerThread(this.path, threadHeaders, this.dataBlockOffset);
            threads.add(thread);
            thread.start();
        }

        for (UnpackerThread thread : threads)
            thread.join();
    }

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
    public ArrayList<Header> getHeaders() {
        return this.headers;
    }
    public long getDataBlockOffset() {
        return this.dataBlockOffset;
    }
}
