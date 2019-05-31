package com.jpbo;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PBOInputStream extends FileInputStream {

    public PBOInputStream(String filepath) throws IOException {
        super(filepath);
    }

    public PBOInputStream(String filepath, long dataOffset) throws IOException {
        super(filepath);
        this.skip(dataOffset);
    }

    public long getPosition() throws IOException {
        return this.getChannel().position();
    }

    public String readString() throws IOException {
        ByteArrayOutputStream byteString = new ByteArrayOutputStream();
        for (int index = this.read(); index != 0; index = this.read())
            byteString.write(index);
        return byteString.toString();
    }

    public PackingMethod readPackingMethod() throws IOException {
        return PackingMethod.fromValue(this.readIntLE());
    }

    public int readIntLE() throws IOException {
        byte[] buffer = new byte[4];
        this.read(buffer);
        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
        return byteBuffer.order(ByteOrder.LITTLE_ENDIAN).getInt();
    }
}
