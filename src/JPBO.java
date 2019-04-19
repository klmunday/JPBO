import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class JPBO {
    
    public static void main(String[] args) throws IOException, InterruptedException {
        if (PBO.validPBOFile("test.pbo")) {
            long startTime = System.currentTimeMillis();
            PBO pbo = PBO.read("test.pbo");

            int threadCount = 8;
            int pboHeaderCount = pbo.getHeaders().size();

            if (threadCount > pboHeaderCount) {
                System.out.println("Thread count greater than header count. Defaulting to header count");
                threadCount = pbo.getHeaders().size();
            }

            ArrayList<UnpackerThread> threads = new ArrayList<>();
            Collections.sort(pbo.getHeaders());  // Sort Headers by DataSize for load balancing

            for (int i = 0; i < threadCount; i++) {
                ArrayList<Header> threadHeaders = new ArrayList<>();

                for (int j = i; j < pboHeaderCount; j += threadCount)
                    threadHeaders.add(pbo.getHeaders().get(j));

                UnpackerThread thread = new UnpackerThread(pbo.getPath(), threadHeaders, pbo.getDataBlockOffset());
                threads.add(thread);
                thread.start();
            }

            for (UnpackerThread thread : threads)
                thread.join();

            long endTime = System.currentTimeMillis();
            System.out.println("Completed in: " + (float)(endTime - startTime) / 1000 + " seconds" );
        } else {
            System.err.println("Please input a valid PBO file");
        }
    }
}
