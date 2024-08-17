package net.querz.mcaselector.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LimitedInputStream extends FilterInputStream {
    private int limit;
    private int markLimit;

    public LimitedInputStream(InputStream in, int limit) {
        super(in);
        this.limit = limit;
    }

    @Override
    public int read() throws IOException {
        limit--;
        if (limit < 0) {
            throw new IOException("Limit reached");
        }
        return super.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        len = Math.min(len, limit);
        int actualRead = super.read(b, off, len);
        limit -= actualRead;
        return actualRead;
    }

    @Override
    public long skip(long n) throws IOException {
        n = Math.min(n, limit);
        long actualSkip = super.skip(n);
        limit -= actualSkip;
        return actualSkip;
    }

    @Override
    public int available() throws IOException {
        return Math.min(super.available(), limit);
    }

    @Override
    public void mark(int readlimit) {
        this.markLimit = limit;
        super.mark(readlimit);
    }

    @Override
    public void reset() throws IOException {
        this.limit = markLimit;
        super.reset();
    }
}
