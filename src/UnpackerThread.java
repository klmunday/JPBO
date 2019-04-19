import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class UnpackerThread extends Thread {

    private String path;
    private ArrayList<Header> headers;
    private long dataBlockOffset;

    public UnpackerThread(String path, ArrayList<Header> headers, long dataBlockOffset) {
        this.path = path;
        this.headers = headers;
        this.dataBlockOffset = dataBlockOffset;
        System.out.println("Thread created with " + headers.size() + " headers");
    }

    @Override
    public void run() {
        File pboDirectory = new File(this.path.substring(0, this.path.length() - 4));
        pboDirectory.mkdir();

        for (Header header : this.headers) {
            //System.out.println("Unpacking " + header.getPath());
            try (PBOInputStream pboReader = new PBOInputStream(this.path)) {
                byte[] dataBuffer = new byte[(int) header.getDataSize()];
                pboReader.skip(this.dataBlockOffset + header.getDataOffset());
                pboReader.read(dataBuffer, 0, (int) header.getDataSize());

                if (header.getPackingMethod().equals(PackingMethod.COMPRESSED)) {
                    // TODO: add LZSS decompression
                    System.out.println(header.getPath() + " has data that needs decompressing");
                }

                File outFile = new File(pboDirectory + "\\" + header.getPath());
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
