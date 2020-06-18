/**
 *
 */
package com.berwin.logger.views.dialogs;

import com.berwin.logger.views.MainView;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author Administrator
 */
public class SelectedDialog extends BaseDialog {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private JComboBox<String> cbPackages = null;

    public SelectedDialog(java.util.List<String> packages, String selected, ItemListener listener) {
        super(MainView.self, "请选择", 0.75f);
        this.setSize(new Dimension(300, 60));
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        this.setLocation(pointerInfo.getLocation());

        cbPackages = new JComboBox();
        this.add(cbPackages);
        this.updatePackageList(packages, selected);
        cbPackages.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                listener.itemStateChanged(e);
                SelectedDialog.this.dispose();
            }
        });
    }

    public void updatePackageList(java.util.List<String> packages, String selected) {
        cbPackages.removeAllItems();
        int selectIdx = 0;
        int i = 0;
        for (String pkg : packages) {
            cbPackages.addItem(pkg);
            if (pkg.equals(selected))
                selectIdx = i;
            i++;
        }
        cbPackages.setSelectedIndex(selectIdx);
    }

    public String getSelectedItem() {
        return (String) cbPackages.getSelectedItem();
    }
}
