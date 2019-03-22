import java.io.*;

public class PBOInputStream extends DataInputStream {

    public PBOInputStream(InputStream in) {
        super(in);
    }

    public String readString() {
        try {
            ByteArrayOutputStream byteString = new ByteArrayOutputStream();
            for (byte index = this.readByte(); index != 0; index = this.readByte()) {
                byteString.write(index);
            }
            return byteString.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public PackingMethod readPackingMethod() {
        try {
            return PackingMethod.fromValue(this.readInt());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
