import java.io.*;

public class jbpo {
    
    public static void main(String[] args) throws IOException {
        if (PBO.validPBOFile("test.pbo")) {
            PBO pbo = PBO.read("test.pbo");
            pbo.unpackFiles();
        } else {
            System.err.println("Please input a valid PBO file");
        }
    }
}
