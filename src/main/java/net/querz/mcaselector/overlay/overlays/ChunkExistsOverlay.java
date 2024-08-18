package net.querz.mcaselector.overlay.overlays;

import net.querz.mcaselector.io.mca.Chunk;
import net.querz.mcaselector.io.mca.ChunkData;
import net.querz.mcaselector.overlay.Overlay;
import net.querz.mcaselector.overlay.OverlayType;

import java.util.Locale;

public class ChunkExistsOverlay extends Overlay {
    public ChunkExistsOverlay() {
        super(OverlayType.CHUNK_EXISTS);
        setMultiValues(new String[0]);
    }

    @Override
    public int parseValue(ChunkData chunkData) {
        Chunk c = switch (getRawMultiValues()) {
            case "poi" -> chunkData.poi();
            case "entities" -> chunkData.entities();
            case null, default -> chunkData.region();
        };
        return c == null ? 0 : 1;
    }

    @Override
    public String name() {
        return "ChunkExists";
    }

    @Override
    public boolean setMin(String raw) {
        setRawMin(raw);
        try {
            return setMinInt(Integer.parseInt(raw));
        } catch (NumberFormatException ex) {
            return setMinInt(null);
        }
    }

    @Override
    public boolean setMax(String raw) {
        setRawMax(raw);
        try {
            return setMaxInt(Integer.parseInt(raw));
        } catch (NumberFormatException ex) {
            return setMaxInt(null);
        }
    }

    @Override
    public boolean setMultiValuesString(String raw) {
        switch (raw == null ? null : raw.toLowerCase(Locale.ROOT)) {
            case null -> {
                setRawMultiValues("");
                return true;
            }
            case "", "region", "entities", "poi" -> {
                setRawMultiValues(raw);
                return true;
            }
            default -> {
                return false;
            }
        }
    }
}
