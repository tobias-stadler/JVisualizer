package de.tost.jvisualizer.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class AddWidget {

    public interface AddWidgetListener {
        void widgetAdded(Class<? extends CanvasAdapter> adapterClazz);
    }

    private ArrayList<Class<? extends CanvasAdapter>> widgetTypes = new ArrayList<>();

    private AddWidgetListener listener;

    public AddWidget() {

    }

    public void addWidgetType(Class<? extends CanvasAdapter> clazz) {
        widgetTypes.add(clazz);
    }

    public void setListener(AddWidgetListener listener) {
        this.listener = listener;
    }

    public void chooseWidget(int xPos, int yPos) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Choose a widget");
        dialog.setLocation(xPos, yPos);
        dialog.setResizable(false);
        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        String[] names = getWidgetNames();
        JComboBox<String> typeBox = new JComboBox<>(names);
        JButton doneButton = new JButton("Add Selected Widget");
        JButton cancelButton = new JButton("Cancel");
        JPanel panel = new JPanel();
        JPanel topPanel = new JPanel();
        JPanel bottomPanel = new JPanel();

        panel.setLayout(new BorderLayout());
        panel.add(topPanel, BorderLayout.PAGE_START);
        panel.add(bottomPanel, BorderLayout.PAGE_END);

        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));

        topPanel.add(typeBox);
        bottomPanel.add(cancelButton);
        bottomPanel.add(doneButton);

        Runnable cancelRunner = () -> {
            if (listener != null) {
                listener.widgetAdded(null);
            }
            dialog.dispose();
        };

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cancelRunner.run();
            }
        });

        cancelButton.addActionListener(e -> {
            cancelRunner.run();
        });
        doneButton.addActionListener(e -> {
            Class<? extends CanvasAdapter> clazz = widgetTypes.get(typeBox.getSelectedIndex());
            if (listener != null && clazz != null) {
                listener.widgetAdded(clazz);
            }
            dialog.dispose();
        });

        dialog.getContentPane().add(panel);
        dialog.pack();
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
    }

    private String[] getWidgetNames() {
        String[] out = new String[widgetTypes.size()];
        for (int i = 0; i < out.length; i++) {
            try {
                Method nameMethod = widgetTypes.get(i).getMethod("getName");
                Object nameObj = nameMethod.invoke(null);
                if (!(nameObj instanceof String)) {
                    throw new Exception();
                }
                out[i] = (String) nameObj;
            } catch (Exception e) {
                out[i] = widgetTypes.get(i).getSimpleName();
            }
        }
        return out;
    }

}
