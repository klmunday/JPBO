import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PBOInputStream extends DataInputStream {

    public PBOInputStream(InputStream in) {
        super(in);
    }

    public String readString() throws IOException {
        ByteArrayOutputStream byteString = new ByteArrayOutputStream();
        for (byte index = this.readByte(); index != 0; index = this.readByte()) {
            byteString.write(index);
        }
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
