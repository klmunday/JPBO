package com.jpbo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class PBOStrings extends ArrayList<String> {

    public PBOStrings() {
        super();
    }

    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        for (String str : this) {
            output.write(str.getBytes());
            output.write(0);
        }
        output.write(0);
        return output.toByteArray();
    }
}
