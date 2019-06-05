package com.jpbo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PBOHeader implements Comparable<PBOHeader> {

    protected String path;
    protected PackingMethod packingMethod;
    protected long originalSize;
    protected int reserved;
    protected int timestamp;
    protected long dataSize;
    private long dataOffset;
    private long headerOffset;

    public PBOHeader(String path, PackingMethod packingMethod, long originalSize,
                     int reserved, int timestamp, long dataSize, long dataOffset, long headerOffset) {
        this.path = path;
        this.packingMethod = packingMethod;
        this.originalSize = originalSize;
        this.reserved = reserved;
        this.timestamp = timestamp;
        this.dataSize = dataSize;
        this.dataOffset = dataOffset;
        this.headerOffset = headerOffset;
    }

    public static PBOHeader read(PBOInputStream pboReader, long dataOffset, long headerOffset) throws IOException {
        String headerName = pboReader.readString();
        PackingMethod packingMethod = pboReader.readPackingMethod();
        long originalSize = pboReader.readIntLE();
        int reserved = pboReader.readIntLE();
        int timestamp = pboReader.readIntLE();
        long dataSize = pboReader.readIntLE();
            
        return new PBOHeader(headerName, packingMethod, originalSize, reserved, timestamp, dataSize, dataOffset, headerOffset);
    }

    @Override
    public String toString() {
        return "PBOHeader(" + this.path + ")"
                + "\n\tMethod: " + this.packingMethod
                + "\n\tOriginal Size: " + this.originalSize
                + "\n\tReserved: " + this.reserved
                + "\n\tTimestamp: " + this.timestamp
                + "\n\tData Size: " + this.dataSize
                + "\n";
    }

    @Override
    public int compareTo(PBOHeader other) {
        return Long.compare(this.dataSize, other.dataSize);
    }

    public boolean isEmpty() {
        return path.isEmpty();
    }

    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        outStream.write(this.path.getBytes());
        outStream.write(0);

        byte[] buffer = new byte[4];
        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

        for (int val : new int[]{this.packingMethod.getValue(), (int) this.originalSize,
                this.reserved, this.timestamp, (int) this.dataSize}) {
            byteBuffer.putInt(val);
            outStream.write(byteBuffer.array());
            byteBuffer.position(0);
        }

        return outStream.toByteArray();
    }

    public int length() throws IOException {
        return this.path.length() + 21;
    }

    public static byte[] emptyHeader() {
        return new byte[21];
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getOriginalSize() {
        return this.originalSize;
    }

    public void setOriginalSize(long originalSize) {
        this.originalSize = originalSize;
    }

    public long getDataSize() {
        return this.dataSize;
    }

    public void setDataSize(long dataSize) {
        this.dataSize = dataSize;
    }

    public long getDataOffset() {
        return this.dataOffset;
    }

    public void setDataOffset(long dataOffset) {
        this.dataOffset = dataOffset;
    }

    public long getHeaderOffset() {
        return this.headerOffset;
    }

    public void setHeaderOffset(long headerOffset) {
        this.headerOffset = headerOffset;
    }

    public PackingMethod getPackingMethod() {
        return this.packingMethod;
    }

    public void setPackingMethod(PackingMethod packingMethod) {
        this.packingMethod = packingMethod;
    }

    public int getReserved() {
        return this.reserved;
    }

    public int getTimestamp() {
        return this.timestamp;
    }
}
