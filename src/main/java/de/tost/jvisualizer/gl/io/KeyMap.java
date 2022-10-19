package de.tost.jvisualizer.gl.io;

import org.lwjgl.glfw.GLFW;

public class KeyMap {

    public static String glfwKeyCodeToName(int keyCode){
        return GLFW.glfwGetKeyName(keyCode, 0);
    }

    public static String scanCodeToName(int scanCode){
        return GLFW.glfwGetKeyName(GLFW.GLFW_KEY_UNKNOWN, scanCode);
    }

    public static int glfwKeyCodeToScanCode(int keyCode){
        return GLFW.glfwGetKeyScancode(keyCode);
    }

}
