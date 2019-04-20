import java.io.*;

public class JPBO {
    
    public static void main(String[] args) throws IOException, InterruptedException {
        String filepath = "test.pbo";
        if (PBO.validPBOFile(filepath)) {
            long startTime = System.currentTimeMillis();
            PBO pbo = PBO.read(filepath);

            System.out.println(pbo);

            int threadCount = 8;
            pbo.unpack(threadCount);

            long endTime = System.currentTimeMillis();
            System.out.println("\nPBO file read and unpacked in: " + (float) (endTime - startTime) / 1000 + " seconds" );
        } else {
            System.err.println("Please input a valid PBO file");
        }
    }
}
