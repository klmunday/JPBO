import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

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

    public static PBO read(String filepath) throws IOException {
        ArrayList<String> pboStrings;
        ArrayList<Header> pboHeaders;
        long dataBlockOffset;
        
        try (PBOInputStream pboReader = new PBOInputStream(filepath, 21)) {
            // Read strings
            System.out.println("Reading strings");
            pboStrings = new ArrayList<>();
            for (String str = pboReader.readString(); !str.isEmpty(); str = pboReader.readString()) {
                pboStrings.add(str);
            }
            System.out.println("Read " + pboStrings.size() + " strings");
            
            // Read header entries
            System.out.println("Reading headers");
            pboHeaders = new ArrayList<>();
            long offset = 0;
            for (Header header = Header.read(pboReader, offset); !header.isEmpty(); header = Header.read(pboReader, offset)) {
                pboHeaders.add(header);
                offset += header.getDataSize();
            }
            System.out.println("Read " + pboHeaders.size() + " headers");
            
            dataBlockOffset = pboReader.getChannel().position();
            System.out.println("Data block reached at offset: " + dataBlockOffset + "\n");
        }
        return new PBO(filepath, pboStrings, pboHeaders, dataBlockOffset);
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
