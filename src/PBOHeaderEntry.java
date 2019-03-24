import java.io.IOException;

public class PBOHeaderEntry {

    private String headerName;
    private PackingMethod packingMethod;
    private int originalSize;
    private int reserved;
    private int timestamp;
    private int dataSize;
    private int dataOffset;

    public PBOHeaderEntry(String headerName, PackingMethod packingMethod, int originalSize,
                          int reserved, int timestamp, int dataSize, int dataOffset) {
        this.headerName = headerName;
        this.packingMethod = packingMethod;
        this.originalSize = originalSize;
        this.reserved = reserved;
        this.timestamp = timestamp;
        this.dataSize = dataSize;
        this.dataOffset = dataOffset;
    }

    public static PBOHeaderEntry read(PBOInputStream pboReader, int dataOffset) throws IOException {
        String headerName = pboReader.readString();
        PackingMethod packingMethod = pboReader.readPackingMethod();
        int originalSize = pboReader.readIntLE();
        int reserved = pboReader.readIntLE();
        int timestamp = pboReader.readIntLE();
        int dataSize = pboReader.readIntLE();
            
        return new PBOHeaderEntry(headerName, packingMethod, originalSize, reserved, timestamp, dataSize, dataOffset);
    }

    @Override
    public String toString() {
        return "Header(" + this.headerName + ")" +
                "\n\tMethod: " + this.packingMethod +
                "\n\tOriginal Size: " + this.originalSize +
                "\n\tReserved: " + this.reserved +
                "\n\tTimestamp: " + this.timestamp +
                "\n\tData Size: " + this.dataSize +
                "\n\tData Offset: " + this.dataOffset + "\n";
    }

    public boolean isEmpty() {
        return headerName.isEmpty();
    }

    public String getHeaderName() {
        return this.headerName;
    }
    public int getDataSize() {
        return this.dataSize;
    }
    public int getDataOffset() {
        return this.dataOffset;
    }
}
