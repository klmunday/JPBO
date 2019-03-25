import java.io.IOException;

public class Header {

    private String headerName;
    private PackingMethod packingMethod;
    private int originalSize;
    private int reserved;
    private int timestamp;
    private long dataSize;
    private long dataOffset;

    public Header(String headerName, PackingMethod packingMethod, int originalSize,
                  int reserved, int timestamp, long dataSize, long dataOffset) {
        this.headerName = headerName;
        this.packingMethod = packingMethod;
        this.originalSize = originalSize;
        this.reserved = reserved;
        this.timestamp = timestamp;
        this.dataSize = dataSize;
        this.dataOffset = dataOffset;
    }

    public static Header read(PBOInputStream pboReader, long dataOffset) throws IOException {
        String headerName = pboReader.readString();
        PackingMethod packingMethod = pboReader.readPackingMethod();
        int originalSize = pboReader.readIntLE();
        int reserved = pboReader.readIntLE();
        int timestamp = pboReader.readIntLE();
        long dataSize = pboReader.readIntLE();
            
        return new Header(headerName, packingMethod, originalSize, reserved, timestamp, dataSize, dataOffset);
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
    public long getDataSize() {
        return this.dataSize;
    }
    public long getDataOffset() {
        return this.dataOffset;
    }
}
