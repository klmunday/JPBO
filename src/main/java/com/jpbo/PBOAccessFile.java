package com.jpbo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PBOAccessFile extends RandomAccessFile {

    private PBO pbo;
    private MessageDigest checkSum;

    public PBOAccessFile(PBO pbo, String path) throws FileNotFoundException, NoSuchAlgorithmException {
        super(path, "rw");
        this.pbo = pbo;
        this.checkSum = MessageDigest.getInstance("SHA-1");
    }

    public PBOAccessFile(PBO pbo) throws FileNotFoundException, NoSuchAlgorithmException {
        this(pbo, pbo.getPath());
    }

    @Override
    public void write(int b) throws IOException {
        this.write(new byte[]{(byte) b});
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        super.write(b, off, len);
        this.checkSum.update(b);
    }

    public void seekAndUpdateChecksum(long pos) throws IOException {
        if (pos <= this.getPosition()) {
            System.err.println("seekAndUpdateChecksum called with lower value than current position.");
            this.seek(pos);
        } else {
            byte[] data = new byte[(int) (pos - this.getPosition())];
            this.read(data);
            this.checkSum.update(data);
        }
    }

    public long getPosition() throws IOException {
        return this.getChannel().position();
    }

    public void writeChecksum() throws IOException {
        super.write(0);
        super.write(this.checkSum.digest());
    }

    public void readAndWrite(long readPos, long readLen) throws IOException {
        byte[] data = new byte[(int) readLen];
        long writePos = this.getPosition();
        this.seek(readPos);
        this.read(data);
        this.seek(writePos);
        this.write(data);
    }

    public void deleteEntry(PBOHeader header) throws IOException {
        this.seekAndUpdateChecksum(header.getHeaderOffset());

        long readFromPos = this.getPosition() + header.length();
        this.readAndWrite(readFromPos, pbo.getDataBlockOffset() + header.getDataOffset() - readFromPos);

        readFromPos = this.getPosition() + header.getDataSize() + header.length();
        // skip data block checksum if header is compressed (TODO: test)
        if (header.getPackingMethod().equals(PackingMethod.COMPRESSED))
            readFromPos += 4;
        this.readAndWrite(readFromPos, this.length() - readFromPos - 21);

        this.writeChecksum();
        this.setLength(this.length() - header.length() - header.getDataSize());
    }

    public void savePBO() throws IOException {
        this.write(pbo.getProductHeader().toBytes());

        for (PBOHeader header : pbo.getHeaders())
            this.write(header.toBytes());

        this.write(PBOHeader.emptyHeader());

        for (PBOHeader header : pbo.getHeaders()) {
            long offset = pbo.getDataBlockOffset() + header.getDataOffset();
            this.readAndWrite(offset, header.getDataSize());
        }

        this.writeChecksum();
        this.close();
    }
}
