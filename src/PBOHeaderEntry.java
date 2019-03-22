public class PBOHeaderEntry {

    private String headerName;
    private PackingMethod packingMethod;
    private int originalSize;
    private int reserved;
    private int timestamp;
    private int dataSize;

    public PBOHeaderEntry(String headerName, PackingMethod packingMethod, int originalSize, int reserved, int timestamp, int dataSize) {
        this.headerName = headerName;
        this.packingMethod = packingMethod;
        this.originalSize = originalSize;
        this.reserved = reserved;
        this.timestamp = timestamp;
        this.dataSize = dataSize;
    }

    public static PBOHeaderEntry read(PBOInputStream pboReader) {
        try {
            String headerName = pboReader.readString();
            PackingMethod packingMethod = pboReader.readPackingMethod();
            int originalSize = pboReader.readInt();
            int reserved = pboReader.readInt();
            int timestamp = pboReader.readInt();
            int dataSize = pboReader.readInt();
            
            return new PBOHeaderEntry(headerName, packingMethod, originalSize, reserved, timestamp, dataSize);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isEmpty() {
        return headerName.isEmpty() && originalSize.equals(0) && dataSize.equals(0);
    }
}
