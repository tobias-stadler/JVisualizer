package de.tost.jvisualizer.gl.io;

import org.lwjgl.glfw.GLFW;

public class KeyModifier {
    private boolean shiftK, controlK, altK, superK;

    public KeyModifier(int mods) {
        decode(mods);
    }

    public KeyModifier(boolean shiftKey, boolean controlKey, boolean altKey, boolean superKey) {
        this.shiftK = shiftKey;
        this.controlK = controlKey;
        this.altK = altKey;
        this.superK = superKey;
    }

    public void decode(int mods) {
        shiftK = false;
        controlK = false;
        altK = false;
        superK = false;

        if ((mods & GLFW.GLFW_MOD_SHIFT) != 0) {
            shiftK = true;
        } else if ((mods & GLFW.GLFW_MOD_CONTROL) != 0) {
            controlK = true;
        } else if ((mods & GLFW.GLFW_MOD_ALT) != 0) {
            altK = true;
        } else if ((mods & GLFW.GLFW_MOD_SUPER) != 0) {
            superK = true;
        }
    }

    public boolean isShiftDown() {
        return shiftK;
    }

    public boolean isControlDown() {
        return controlK;
    }

    public boolean isAltDown() {
        return altK;
    }

    public boolean isSuperDown() {
        return superK;
    }

}
