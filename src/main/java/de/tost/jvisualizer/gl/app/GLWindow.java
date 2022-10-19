package de.tost.jvisualizer.gl.app;

import de.tost.jvisualizer.gl.io.ButtonAction;
import de.tost.jvisualizer.gl.io.KeyModifier;
import de.tost.jvisualizer.gl.io.MouseButton;
import org.lwjgl.glfw.GLFWDropCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GLWindow {

    private Thread thread;

    private long windowHandle;
    private boolean running = false;

    private final GLApp app;

    public GLWindow(GLApp app) {
        this.app = app;
        if (app != null) {
            app.init();
        }
    }

    public void start() {
        if (running)
            return;
        running = true;
        thread = new Thread(this::run);
        thread.start();
    }

    public void stop() {
        running = false;
    }

    private void run() {
        if (!initWindow()) {
            System.err.println("---Failed INIT of GLWindow!---");
            running = false;
            return;
        }

        app.glInit();
        while (running) {
            if (glfwWindowShouldClose(windowHandle)) {
                running = false;
            }

            glfwPollEvents();
            app.glUpdate();

            app.glRender();
            glfwSwapBuffers(windowHandle);
        }

        app.cleanUp();
        cleanUp();

        running = false;
    }

    private boolean initWindow() {
        if (app == null) {
            System.err.println("App was null");
            return false;
        }

        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            System.err.println("GLFW: failed INIT!");
            return false;
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, app.isResizeable() ? GLFW_TRUE : GLFW_FALSE);

        /*
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        */


        windowHandle = glfwCreateWindow(app.getWidth(), app.getHeight(), app.getTitle(), NULL, NULL);
        if (windowHandle == NULL) {
            System.err.println("GLFW: failed WINDOW CREATION!");
            return false;
        }

        setupCallbacks();

        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(windowHandle, (vidMode.width() - app.getWidth()) / 2, (vidMode.height() - app.getHeight()) / 2);
        glfwMakeContextCurrent(windowHandle);
        glfwSwapInterval(1);
        glfwShowWindow(windowHandle);

        GL.createCapabilities();

        System.out.println("OpenGL Version: " + glGetString(GL_VERSION));

        return true;
    }

    private void setupCallbacks() {
        glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
            if (window == windowHandle && app != null) {
                KeyModifier modifier = new KeyModifier(mods);
                ButtonAction enumAction;
                if(action == GLFW_PRESS){
                    enumAction = ButtonAction.PRESSED;
                } else if(action == GLFW_RELEASE){
                    enumAction = ButtonAction.RELEASED;
                } else if(action == GLFW_REPEAT){
                    enumAction = ButtonAction.REPEAT;
                } else {
                    enumAction = null;
                }
                if(enumAction != null){
                    app.onKeyEvent(key, scancode, enumAction, modifier, false);
                }
            }
        });
        glfwSetScrollCallback(windowHandle, (window, xoffset, yoffset) -> {
            if (window == windowHandle && app != null) {
                app.onScrollEvent(xoffset, yoffset, false);
            }
        });
        glfwSetCursorPosCallback(windowHandle, (window, xpos, ypos) -> {
            if (window == windowHandle && app != null) {
                app.onCursorPositionEvent(xpos, ypos, false);
            }
        });
        glfwSetFramebufferSizeCallback(windowHandle, (window, width, height) -> {
            if (window == windowHandle && app != null) {
                app.onFramebufferSizeEvent(width, height);
            }
        });
        glfwSetMouseButtonCallback(windowHandle, (window, button, action, mods) -> {
            if (window == windowHandle && app != null) {
                MouseButton enumButton;
                KeyModifier modifier = new KeyModifier(mods);
                if(button == GLFW_MOUSE_BUTTON_LEFT){
                    enumButton = MouseButton.LEFT;
                } else if(button == GLFW_MOUSE_BUTTON_RIGHT){
                    enumButton = MouseButton.RIGHT;
                } else if(button == GLFW_MOUSE_BUTTON_MIDDLE){
                    enumButton = MouseButton.MIDDLE;
                } else {
                    enumButton = null;
                    //System.out.println("MouseButtonEvent: " + button);
                }
                ButtonAction enumAction;
                if(action == GLFW_PRESS){
                    enumAction = ButtonAction.PRESSED;
                } else if(action == GLFW_RELEASE){
                    enumAction = ButtonAction.RELEASED;
                } else {
                    enumAction = null;
                    //System.out.println("MouseActionEvent: " + action);
                }

                if(enumButton != null && enumAction != null){
                    app.onMouseButtonEvent(enumButton, enumAction, modifier, false);
                }
            }
        });
        glfwSetDropCallback(windowHandle, (window, count, names) -> {
            if(window == windowHandle && app != null){
                String[] filePaths = new String[count];
                for(int i = 0; i < count; i++){
                    filePaths[i] = GLFWDropCallback.getName(names, i);
                }
                app.onFileDropEvent(filePaths, false);
            }
        });
        glfwSetWindowPosCallback(windowHandle, (window, xpos, ypos) -> {
            if(window == windowHandle && app != null){
                app.onWindowPositionEvent(xpos, ypos);
            }
        });
    }


    private void cleanUp() {
        glfwFreeCallbacks(windowHandle);
        glfwDestroyWindow(windowHandle);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
}
