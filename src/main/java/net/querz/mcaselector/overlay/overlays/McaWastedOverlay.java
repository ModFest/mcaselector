package net.querz.mcaselector.overlay.overlays;

import net.querz.mcaselector.io.mca.Chunk;
import net.querz.mcaselector.io.mca.ChunkData;
import net.querz.mcaselector.overlay.Overlay;
import net.querz.mcaselector.overlay.OverlayType;

import java.util.Locale;

/**
 * Displays the amount of space "wasted" by mca's sectioning
 */
public class McaWastedOverlay extends Overlay {
    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 4096; // 384 * 16 * 16

    public McaWastedOverlay() {
        super(OverlayType.MCA_WASTED);
        setMultiValues(new String[0]);
    }

    @Override
    public int parseValue(ChunkData chunkData) {
        Chunk c = switch (getRawMultiValues()) {
            case "poi" -> chunkData.poi();
            case "entities" -> chunkData.entities();
            case null, default -> chunkData.region();
        };
        if (c == null) {
            return 0;
        }
        if (c.getOriginalSize() % 4096 == 0) {
            return 0;
        } else {
            return 4096 - (c.getOriginalSize() % 4096);
        }
    }

    @Override
    public String name() {
        return "McaWasted";
    }

    @Override
    public boolean setMin(String raw) {
        setRawMin(raw);
        try {
            int value = Integer.parseInt(raw);
            if (value < MIN_VALUE || value > MAX_VALUE) {
                return setMinInt(null);
            }
            return setMinInt(value);
        } catch (NumberFormatException ex) {
            return setMinInt(null);
        }
    }

    @Override
    public boolean setMax(String raw) {
        setRawMax(raw);
        try {
            int value = Integer.parseInt(raw);
            if (value < MIN_VALUE || value > MAX_VALUE) {
                return setMaxInt(null);
            }
            return setMaxInt(value);
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
