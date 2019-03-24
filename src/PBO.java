import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;

public class PBO {

    private String filename;
    private String path;
    private ArrayList<String> strings;
    private ArrayList<PBOHeaderEntry> headers;
    private int dataBlockOffset;

    public PBO(String path, ArrayList<String> strings, ArrayList<PBOHeaderEntry> headers, int dataBlockOffset) {
        this.filename = filename;
        this.path = path;
        this.strings = strings;
        this.headers = headers;
        this.dataBlockOffset = dataBlockOffset;
    }

    public static PBO read(String filepath) throws IOException {
        FileInputStream pboFile = new FileInputStream(filepath);
        PBOInputStream pboReader = new PBOInputStream(pboFile);
        pboFile.skip(21);  // Skip product header

        // Read strings
        System.out.println("Reading strings");
        ArrayList<String> pboStrings = new ArrayList<>();
        String curString = pboReader.readString();
        while (!curString.isEmpty()) {
            pboStrings.add(curString);
            System.out.println("\tString: \"" + curString + "\"");
            curString = pboReader.readString();
        }
        System.out.println("Read " + pboStrings.size() + " strings\n");

         // Read header entries
        System.out.println("Reading headers");
        ArrayList<PBOHeaderEntry> pboHeaders = new ArrayList<>();
        int dataOffset = 0;
        PBOHeaderEntry header = PBOHeaderEntry.read(pboReader, dataOffset);
        while (!header.isEmpty()) {
            pboHeaders.add(header);
            System.out.println(header.toString());
            dataOffset += header.getDataSize();
            header = PBOHeaderEntry.read(pboReader, dataOffset);
        }
        System.out.println("Read " + pboHeaders.size() + " headers\n");

        int dataBlockOffset = (int) pboFile.getChannel().position();
        System.out.println("Data block reached at offset: " + dataBlockOffset);

        pboFile.close();
        return new PBO(filepath, pboStrings, new ArrayList<>(), dataBlockOffset);
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
