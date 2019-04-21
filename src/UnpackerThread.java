import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class UnpackerThread extends Thread {

    private String path;
    private String outputDir;
    private ArrayList<Header> headers;
    private long dataBlockOffset;

    public UnpackerThread(String path, String outputDir, ArrayList<Header> headers, long dataBlockOffset) {
        this.path = path;
        this.outputDir = outputDir;
        this.headers = headers;
        this.dataBlockOffset = dataBlockOffset;
    }

    @Override
    public void run() {
        File pboDirectory = new File(this.outputDir);

        for (Header header : this.headers) {
            System.out.println("Unpacking " + header.getPath());

            try (PBOInputStream pboReader = new PBOInputStream(this.path)) {
                byte[] dataBuffer = new byte[(int) header.getDataSize()];

                pboReader.skip(this.dataBlockOffset + header.getDataOffset());
                pboReader.read(dataBuffer, 0, (int) header.getDataSize());

                // TODO: add LZSS decompression
                if (header.getPackingMethod().equals(PackingMethod.COMPRESSED))
                    System.out.println(header.getPath() + " skipped - LZSS decompression not yet supported");

                String filename = pboDirectory + File.separator + header.getPath().replace("\\", File.separator);
                File outFile = new File(filename);
                outFile.getParentFile().mkdirs();

                FileOutputStream fileOut = new FileOutputStream(outFile);
                fileOut.write(dataBuffer);
                fileOut.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
