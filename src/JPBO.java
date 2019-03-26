import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class JPBO {
    
    public static void main(String[] args) throws IOException, InterruptedException {
        if (PBO.validPBOFile("test.pbo")) {
            long startTime = System.currentTimeMillis();
            PBO pbo = PBO.read("test.pbo");

            int threadCount = 4;

            if (threadCount > pbo.getHeaders().size())
                throw new IllegalArgumentException("Thread count cannot be larger than PBO header count");

            UnpackerThread[] threads = new UnpackerThread[threadCount];
            for (int i = 0; i < threadCount; i++) {
                ArrayList<Header> pboHeaders = pbo.getHeaders();
                double div = pboHeaders.size() / (double) threadCount;
                int headerChunkSize = (int) Math.ceil(div);

                List<Header> headerChunk;
                if ((i == threadCount - 1) && (div % 1 != 0))
                    headerChunk = pboHeaders.subList(i * headerChunkSize, ((i + 1) * headerChunkSize) - i);
                else
                    headerChunk = pboHeaders.subList(i * headerChunkSize, (i + 1) * headerChunkSize);

                UnpackerThread thread = new UnpackerThread(pbo.getPath(), new ArrayList<>(headerChunk), pbo.getDataBlockOffset());
                threads[i] = thread;
                thread.start();
            }

            for (UnpackerThread thread : threads) {
                thread.join();
            }

            long endTime = System.currentTimeMillis();
            System.out.println("Completed in: " + (float)(endTime - startTime) / 1000 + " seconds" );
        } else {
            System.err.println("Please input a valid PBO file");
        }
    }
}
