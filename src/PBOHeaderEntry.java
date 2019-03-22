import java.io.RandomAccessFile;

public class PBOHeaderEntry {

    private String filename;
    private PackingMethod packingMethod;
    private int originalSize;
    private int reserved;
    private int timestamp;
    private int dataSize;

    public PBOHeaderEntry(String filename, PackingMethod packingMethod, int originalSize, int reserved, int timestamp, int dataSize) {
        this.filename = filename;
        this.packingMethod = packingMethod;
        this.originalSize = originalSize;
        this.reserved = reserved;
        this.timestamp = timestamp;
        this.dataSize = dataSize;
    }

    public static PBOHeaderEntry read(RandomAccessFile pboFile) {
        try {
            String filename = pboFile.readUTF();
            PackingMethod packingMethod = PackingMethod.fromValue(pboFile.readInt());
            int originalSize = pboFile.readInt();
            int reserved = pboFile.readInt();
            int timestamp = pboFile.readInt();
            int dataSize = pboFile.readInt();

            return new PBOHeaderEntry(filename, packingMethod, originalSize, reserved, timestamp, dataSize);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
