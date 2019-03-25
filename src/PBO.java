import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;

public class PBO {

    private String filename;
    private String path;
    private ArrayList<String> strings;
    private ArrayList<Header> headers;
    private long dataBlockOffset;

    public PBO(String path, ArrayList<String> strings, ArrayList<Header> headers, long dataBlockOffset) {
        this.filename = filename;
        this.path = path;
        this.strings = strings;
        this.headers = headers;
        this.dataBlockOffset = dataBlockOffset;
    }

    public static PBO read(String filepath) throws IOException {
        PBOInputStream pboReader = new PBOInputStream(filepath);

        // Read strings
        System.out.println("Reading strings");
        ArrayList<String> pboStrings = new ArrayList<>();

        for (String str = pboReader.readString(); !str.isEmpty(); str = pboReader.readString()) {
            pboStrings.add(str);
            System.out.println("\tString: \"" + str + "\"");
        }
        System.out.println("\nRead " + pboStrings.size() + " strings\n");

         // Read header entries
        System.out.println("Reading headers");
        ArrayList<Header> pboHeaders = new ArrayList<>();
        long offset = 0;

        for (Header header = Header.read(pboReader, offset); !header.isEmpty(); header = Header.read(pboReader, offset)) {
            pboHeaders.add(header);
            offset += header.getDataSize();
            System.out.println(header.toString());
        }
        System.out.println("Read " + pboHeaders.size() + " headers\n");

        long dataBlockOffset = pboReader.getChannel().position();
        System.out.println("Data block reached at offset: " + dataBlockOffset);

        pboReader.close();
        return new PBO(filepath, pboStrings, pboHeaders, dataBlockOffset);
    }

    public static Boolean validPBOFile(String filepath) throws IOException {
        RandomAccessFile pboFile = new RandomAccessFile(filepath, "r");
        pboFile.seek(1);

        byte[] magic = new byte[4];
        pboFile.read(magic);
        pboFile.close();

        boolean fileIsPBO = Arrays.equals(magic, new byte[]{115, 114, 101, 86});  // check magic number
        return fileIsPBO && filepath.endsWith(".pbo");
    }

    public static void main(String[] args) throws IOException {
        if (validPBOFile("test.pbo")) {
            PBO pbo = PBO.read("test.pbo");
        } else {
            System.err.println("Please input a valid PBO file");
        }
    }
}
