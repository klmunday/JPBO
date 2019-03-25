import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.File;

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
        
        try (PBOInputStream pboReader = new PBOInputStream(filepath)) {
            // Read strings
            System.out.println("Reading strings");
            pboStrings = new ArrayList<>();
            for (String str = pboReader.readString(); !str.isEmpty(); str = pboReader.readString()) {
                pboStrings.add(str);
                System.out.println("\tString: \"" + str + "\"");
            }   System.out.println("\nRead " + pboStrings.size() + " strings\n");
            
            // Read header entries
            System.out.println("Reading headers");
            pboHeaders = new ArrayList<>();
            long offset = 0;
            for (Header header = Header.read(pboReader, offset); !header.isEmpty(); header = Header.read(pboReader, offset)) {
                pboHeaders.add(header);
                offset += header.getDataSize();
                System.out.println(header.toString());
            }   System.out.println("Read " + pboHeaders.size() + " headers");
            
            dataBlockOffset = pboReader.getChannel().position();
            System.out.println("Data block reached at offset: " + dataBlockOffset + "\n");
        }
        return new PBO(filepath, pboStrings, pboHeaders, dataBlockOffset);
    }
    
    public void unpackFiles() throws IOException {
        for (Header header : this.headers) {
            System.out.println("Unpacking " + header.getPath());
        }
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
}
