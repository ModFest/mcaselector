package net.querz.mcaselector.io;

import net.querz.mcaselector.ChunkDataProcessor;
import net.querz.mcaselector.ColorMapping;
import net.querz.nbt.CompoundTag;
import net.querz.nbt.NBTInputStream;
import net.querz.nbt.Tag;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public class MCAChunkData {

	private long offset; //in actual bytes
	private int timestamp;
	private byte sectors;
	private int length; //length without padding
	private CompressionType compressionType;
	private CompoundTag data;

	//offset in 4KiB chunks
	public MCAChunkData(int offset, int timestamp, byte sectors) {
		this.offset = ((long) offset) * 4096;
		this.timestamp = timestamp;
		this.sectors = sectors;
	}

	public boolean isEmpty() {
		return offset == 0 && timestamp == 0 && sectors == 0;
	}

	public void readHeader(RandomAccessFile raf) throws Exception {
		raf.seek(offset);
		length = raf.readInt();
		compressionType = CompressionType.fromByte(raf.readByte());
	}

	public void loadData(RandomAccessFile raf) throws Exception {
		raf.seek(offset + 5);
		MCAInputStream in = new MCAInputStream(raf);
		NBTInputStream nbtIn = null;
		Tag tag;

		try {
			switch (compressionType) {
			case GZIP:
				nbtIn = new NBTInputStream(new BufferedInputStream(new GZIPInputStream(in)));
				break;
			case ZLIB:
				nbtIn = new NBTInputStream(new BufferedInputStream(new InflaterInputStream(in)));
				break;
			case NONE:
				data = null;
				return;
			}
			tag = nbtIn.readTag();
		} finally {
			if (nbtIn != null) {
				nbtIn.close();
			}
		}

		if (tag instanceof CompoundTag) {
			data = (CompoundTag) tag;
		} else {
			throw new Exception("Invalid chunk data: tag is not of type CompoundTag");
		}
	}

	public void drawImage(ChunkDataProcessor chunkDataProcessor, ColorMapping colorMapping, int x, int z, BufferedImage image) {
		if (data != null) {
			chunkDataProcessor.drawImage(data, colorMapping, x, z, image);
		}
	}

	public long getOffset() {
		return offset;
	}

	void setOffset(int sectorOffset) {
		this.offset = ((long) sectorOffset) * 4096;
	}

	public int getLength() {
		return length;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public byte getSectors() {
		return sectors;
	}

	public CompressionType getCompressionType() {
		return compressionType;
	}

	public CompoundTag getData() {
		return data;
	}
}
