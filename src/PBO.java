import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;

public class PBO {

    private String filename;
    private String path;
    private ArrayList<String> strings;
    private ArrayList<PBOHeaderEntry> headers;

    public PBO(String path, ArrayList<String> strings, ArrayList<PBOHeaderEntry> headers) {
        this.filename = filename;
        this.path = path;
        this.strings = strings;
        this.headers = headers;
    }

    public static PBO read(String filepath) {
        try {
            FileInputStream pboFile = new FileInputStream(filepath);
            PBOInputStream pboReader = new PBOInputStream(pboFile);
            pboFile.skip(21);  // Skip product header

            // Read strings
            System.out.println("Reading strings");
            ArrayList<String> pboStrings = new ArrayList<>();
            String curString = pboReader.readString();
            while (!curString.isEmpty()) {
                pboStrings.add(curString);
                curString = pboReader.readString();
            }
            System.out.println("Read " + pboStrings.size() + " strings");

            // Read header entries
            System.out.println("Reading headers");
            ArrayList<PBOHeaderEntry> pboHeaders = new ArrayList<>;
            PBOHeaderEntry header = PBOHeaderEntry.read(pboReader);
            while (!header.isEmpty()) {
                pboHeaders.add(header);
                header = PBOHeaderEntry.read(pboReader);
            }
            System.out.println("Read " + pboHeaders.size() + " headers");

            int dataBlockOffset = pboFile.getChannel().getPosition();
            System.out.println("Data block reached at offset: " + dataBlockOffset);

            pboFile.close();
            return new PBO(filepath, pboStrings, new ArrayList<>());
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Boolean validPBOFile(String filepath) {
        try {
            RandomAccessFile pboFile = new RandomAccessFile(filepath, "r");
            pboFile.seek(1);

            byte[] magic = new byte[4];
            pboFile.read(magic);
            pboFile.close();

            boolean fileIsPBO = Arrays.equals(magic, new byte[]{115, 114, 101, 86});  // check magic number
            return fileIsPBO && filepath.endsWith(".pbo");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        if (validPBOFile("test.pbo")) {
            PBO pbo = PBO.read("test.pbo");
        } else {
            System.err.println("Please input a valid PBO file");
        }
    }
}
