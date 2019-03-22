import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class PBO {

    private String filename;
    private String path;
    private ArrayList<String> strings;

    public PBO(String path, ArrayList<String> strings) {
        this.filename = filename;
        this.path = path;
        this.strings = strings;
    }

    public static PBO read(String filepath) {
        try {
            if (validPBOFile(filepath)) {
                FileInputStream pboFile = new FileInputStream(filepath);
                pboFile.skip(21);  // Skip pbo header

                // Read strings
                ArrayList<String> pboStrings = new ArrayList<>();
                Scanner scanner = new Scanner(pboFile, StandardCharsets.UTF_8);
                scanner.useDelimiter("\u0000");
                String curString = scanner.next();
                while (!curString.isEmpty()) {
                    pboStrings.add(curString);
                    System.out.println(curString);
                    curString = scanner.next();
                }
                System.out.println(pboStrings.size());

                pboFile.close();
                return new PBO(filepath, pboStrings);
            } else {
                return null;
            }
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
        PBO pbo = PBO.read("test.pbo");
    }
}
