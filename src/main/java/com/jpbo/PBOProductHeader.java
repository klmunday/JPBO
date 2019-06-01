package com.jpbo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PBOProductHeader extends PBOHeader {

    private PBOStrings strings;

    public PBOProductHeader(String path, PackingMethod packingMethod, long originalSize,
                  int reserved, int timestamp, long dataSize, PBOStrings strings) {
        super(path, packingMethod, originalSize, reserved, timestamp, dataSize, 0, 0);
        this.strings = strings;
    }

    // TODO: make custom exception
    public static PBOProductHeader read(PBOInputStream pboReader) throws Exception {
        String headerName = pboReader.readString();
        PackingMethod packingMethod = pboReader.readPackingMethod();

        if (!packingMethod.equals(PackingMethod.PRODUCT))
            throw new Exception("PBO product entry is invalid, PBO possibly corrupt or not supported.");

        long originalSize = pboReader.readIntLE();
        int reserved = pboReader.readIntLE();
        int timestamp = pboReader.readIntLE();
        long dataSize = pboReader.readIntLE();

        PBOStrings pboStrings = new PBOStrings();
        for (String str = pboReader.readString(); !str.isEmpty(); str = pboReader.readString())
            pboStrings.add(str);

        return new PBOProductHeader(headerName, packingMethod, originalSize, reserved, timestamp, dataSize, pboStrings);
    }

    @Override
    public String toString() {
        return "ProductEntry(" + this.path + ")"
                + "\n\tMethod: " + this.packingMethod
                + "\n\tReserved: " + this.reserved
                + "\n\tTimestamp: " + this.timestamp
                + "\n";
    }

    @Override
    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.write(super.toBytes());
        output.write(this.strings.toBytes());
        return output.toByteArray();
    }

    @Override
    public int length() throws IOException {
        return super.length() + this.strings.toBytes().length;
    }

    public PBOStrings getStrings() {
        return this.strings;
    }
}
