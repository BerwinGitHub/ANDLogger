package com.berwin.logger.views.components;

import com.berwin.logger.entity.Log;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Enumeration;

public class StyleTable extends JTable {
    public static Color COLOR_VERBOSE = Color.DARK_GRAY;
    public static Color COLOR_DEBUG = new Color(124, 174, 252);
    public static Color COLOR_INFO = new Color(110, 195, 46);
    public static Color COLOR_WARNING = new Color(189, 95, 23);
    public static Color COLOR_ERROR = new Color(200, 39, 6);
    public static Color COLOR_FATAL = new Color(220, 43, 10);
    public static Color COLOR_STENCIL = Color.GRAY;

    private java.util.List<Log> logs = new ArrayList<Log>();
    private DefaultTableModel model = null;

    public StyleTable(Object[] columnNames) {
        super();
        this.model = new DefaultTableModel(null, columnNames);
        this.setModel(this.model);
        // 将奇偶行分别设置为不同颜色
        this.paintColorFont();
        // 通过点击表头来排序列中数据resort data by clicking table header
        RowSorter<TableModel> sorter = new TableRowSorter<>(this.getModel());
        this.setRowSorter(sorter);

        // 设置数据与单元格边框的眉边距
//        this.setIntercellSpacing(new Dimension(5, 5));

        // 根据单元内的数据内容自动调整列宽resize column width accordng to content of cell automatically
        this.fitTableColumns(this);
    }


    public void addLog(Log log) {
        this.logs.add(log);
        this.model.addRow(log.toRowData());
        this.fitTableColumns(this);
    }

    public void removeAllItems() {
        while (this.model.getRowCount() > 0) {
            this.model.removeRow(0);
        }
        this.logs.clear();
    }

    /**
     * 将列设置为固定宽度。//fix table column width
     */
    public void setFixColumnWidth(JTable table) {
        //this.setRowHeight(30);
        this.setAutoResizeMode(table.AUTO_RESIZE_OFF);
        /**/
        //The following code can be used to fix table column width
        TableColumnModel tcm = table.getTableHeader().getColumnModel();
        for (int i = 0; i < tcm.getColumnCount(); i++) {
            TableColumn tc = tcm.getColumn(i);
            tc.setPreferredWidth(50);
            // tc.setMinWidth(100);
            tc.setMaxWidth(50);
        }
    }

    /**
     * 根据数据内容自动调整列宽。//resize column width automatically
     */
    public void fitTableColumns(JTable myTable) {
        myTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JTableHeader header = myTable.getTableHeader();
        int rowCount = myTable.getRowCount();
        Enumeration columns = myTable.getColumnModel().getColumns();
        while (columns.hasMoreElements()) {
            TableColumn column = (TableColumn) columns.nextElement();
            int col = header.getColumnModel().getColumnIndex(column.getIdentifier());
            int width = (int) header.getDefaultRenderer().getTableCellRendererComponent
                    (myTable, column.getIdentifier(), false, false, -1, col).getPreferredSize().getWidth();
            for (int row = 0; row < rowCount; row++) {
                int preferedWidth = (int) myTable.getCellRenderer(row, col).getTableCellRendererComponent
                        (myTable, myTable.getValueAt(row, col), false, false, row, col).getPreferredSize().getWidth();
                width = Math.max(width, preferedWidth);
            }
            header.setResizingColumn(column); // 此行很重要
            column.setWidth(width + myTable.getIntercellSpacing().width);
        }
    }

    /**
     * 根据color数组中相应字符串所表示的颜色来设置某行的颜色，注意，JTable中可以对列进行整体操作
     * 而无法对行进行整体操作，故设置行颜色实际上是逐列地设置该行所有单元格的颜色。
     */
    public void paintRow() {
        TableColumnModel tcm = this.getColumnModel();
        for (int i = 0, n = tcm.getColumnCount(); i < n; i++) {
            TableColumn tc = tcm.getColumn(i);
            tc.setCellRenderer(new RowRenderer());
        }
    }

    public void paintColorFont() {
        TableColumnModel tcm = this.getColumnModel();
        for (int i = 0, n = tcm.getColumnCount(); i < n; i++) {
            TableColumn tc = tcm.getColumn(i);
            tc.setCellRenderer(new RowRenderer());
        }
    }

    /**
     * 定义内部类，用于控制单元格颜色，每两行颜色相间，本类中定义为蓝色和绿色。
     *
     * @author Sidney
     */
    private class RowRenderer extends DefaultTableCellRenderer {

        private Color rowColor = COLOR_INFO;
        private int currentRow = -1;

        public Component getTableCellRendererComponent(JTable t, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
//            //设置奇偶行的背景色，可在此根据需要进行修改
//            if (row % 2 == 0)
//                setBackground(Color.GRAY);
//            else
//                setBackground(Color.WHITE);
            if (currentRow != row) {
                currentRow = row;
                String level = StyleTable.this.logs.get(row).getLevel();
                if (level == Log.LEVEL_I) {
                    this.rowColor = COLOR_INFO;
                } else if (level == Log.LEVEL_D) {
                    this.rowColor = COLOR_DEBUG;
                } else if (level == Log.LEVEL_W) {
                    this.rowColor = COLOR_WARNING;
                } else if (level == Log.LEVEL_E) {
                    this.rowColor = COLOR_ERROR;
                } else if (level == Log.LEVEL_F) {
                    this.rowColor = COLOR_FATAL;
                } else if (level == Log.LEVEL_S) {
                    this.rowColor = COLOR_STENCIL;
                } else if (level == Log.LEVEL_V) {
                    this.rowColor = COLOR_VERBOSE;
                }
                setForeground(this.rowColor);
            }
            return super.getTableCellRendererComponent(t, value, isSelected,
                    hasFocus, row, column);
        }

        @Override
        protected void setValue(Object value) {
            super.setValue(value);
        }
    }


//    /**
//     * 定义内部类，设置某行颜色
//     *
//     * @author Sidney
//     */
//    private class FontColorRenderer extends DefaultTableCellRenderer {
//
//        @Override
//        protected void setValue(Object value) {
//            super.setValue(value);
//            setForeground(Color.GREEN);
//        }
//    }


}
