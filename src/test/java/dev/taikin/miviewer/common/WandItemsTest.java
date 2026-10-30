package dev.taikin.miviewer.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import net.kyori.adventure.key.Key;
import org.junit.jupiter.api.Test;

class WandItemsTest {

    @Test
    void exposesStableSharedKeys() {
        assertEquals("miviewer", WandItems.TYPE_KEY.getNamespace());
        assertEquals("wand-type", WandItems.TYPE_KEY.getKey());
        assertEquals("miviewer", WandItems.TARGET_KEY.getNamespace());
        assertEquals("wand-target", WandItems.TARGET_KEY.getKey());
    }

    @Test
    void mapsTypesToSharedItemModels() {
        assertEquals(Key.key("miviewer", "wand_menu"),
                WandItems.modelKey(WandItems.WandType.MENU));
        assertEquals(Key.key("miviewer", "wand_view"),
                WandItems.modelKey(WandItems.WandType.VIEW));
        assertEquals(Key.key("miviewer", "wand_atom"),
                WandItems.modelKey(WandItems.WandType.ATOM));
        assertEquals(Key.key("miviewer", "wand_frame"),
                WandItems.modelKey(WandItems.WandType.FRAME));
    }
}
