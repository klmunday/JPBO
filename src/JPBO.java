import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class JPBO {
    
    public static void main(String[] args) throws IOException, InterruptedException {
        if (PBO.validPBOFile("test.pbo")) {
            long startTime = System.currentTimeMillis();
            PBO pbo = PBO.read("test.pbo");

            int threadCount = 8;
            if (threadCount > pbo.getHeaders().size()) {
                System.out.println("Thread count greater than header count. Defaulting to header count");
                threadCount = pbo.getHeaders().size();
            }

            ArrayList<UnpackerThread> threads = new ArrayList<>();
            Collections.sort(pbo.getHeaders());
            double chunkSize = (double) pbo.getHeaders().size() / threadCount;

            for (int i = 0, processed = 0; i < threadCount; i++, processed += chunkSize) {
                ArrayList<Header> headerSubList = new ArrayList<>();

                for (int j = i; j < pbo.getHeaders().size(); j += threadCount)
                    headerSubList.add(pbo.getHeaders().get(j));

                UnpackerThread thread = new UnpackerThread(pbo.getPath(), headerSubList, pbo.getDataBlockOffset());
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
