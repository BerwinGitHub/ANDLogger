package com.berwin.logger.views.dialogs;

import javax.swing.*;
import java.awt.*;

public class BaseDialog extends JDialog {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    protected JPanel root = null;

    public BaseDialog(Frame parent, String name, float rate) {
        super(parent, name, true);
        int x = parent.getX(), y = parent.getY(), w = parent.getWidth(), h = parent
                .getHeight();
        this.setSize((int) (w * rate), (int) (h * rate));
        this.setLocation((w - this.getWidth()) / 2 + x, (h - this.getHeight())
                / 2 + y);
        this.setResizable(false);
        this.setVisible(false);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.root = new JPanel();
        this.root.setLayout(new BorderLayout());
        this.add(this.root);
    }
}
