package com.berwin.logger.views.components;

import com.berwin.logger.entity.*;
import com.berwin.logger.utility.UserDefault;
import com.berwin.logger.views.MainView;
import org.w3c.dom.css.Rect;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class StyleTable extends JTable {

    private java.util.List<Log> logAll = new ArrayList<>();
    private java.util.List<Log> logFilted = new ArrayList<>();
    private int[] columnMaxWidths;
    private DefaultTableModel model;
    private MainView mainView;
    private Filter filter = null;
    private Find finder = null;
//    private RowSorter<DefaultTableModel> sorter = null;

    private int maxLogRow = 10;

    public StyleTable(MainView frame, Filter filter, Find finder, Object[] columnNames) {
        super();
        this.columnMaxWidths = new int[columnNames.length];
        for (int i = 0; i < this.columnMaxWidths.length; i++)
            this.columnMaxWidths[i] = 0;
        this.maxLogRow = UserDefault.getInstance().getValueForKey("cache_num", 10000);
        this.mainView = frame;
        this.setFilter(filter);
        this.setFinder(finder);
        this.model = new DefaultTableModel(null, columnNames);
        this.setModel(this.model);
        // 将奇偶行分别设置为不同颜色
        this.paintColorFont();
        // 通过点击表头来排序列中数据resort data by clicking table header
//        this.sorter = new TableRowSorter<DefaultTableModel>((DefaultTableModel) this.getModel());
//        this.setRowSorter(this.sorter);

        // 设置数据与单元格边框的眉边距
//        this.setIntercellSpacing(new Dimension(5, 5));

        // 根据单元内的数据内容自动调整列宽resize column width accordng to content of cell automatically
        this.fitTableColumns();
    }


    public void addLog(Log log) {
        this.logAll.add(log);
        if (!this.filter.filted(log)) {
            log.setRow(this.logFilted.size());
            this.logFilted.add(log);
            this.model.addRow(log.toRowData());
            this.tryCheckLogOverflow();
            this.fitTableColumns();
            this.mainView.tryScrollBottom();
        }
        this.mainView.updateLogLines();
    }

    public void updatedFilter() {
        while (this.model.getRowCount() > 0) {
            this.model.removeRow(0);
        }
        this.logFilted.clear();
        for (Log log : this.logAll) {
            if (!this.filter.filted(log)) {
                log.setRow(this.logFilted.size());
                this.logFilted.add(log);
                this.model.addRow(log.toRowData());
                this.fitTableColumns();
            }
        }
        this.tryCheckLogOverflow();
        this.mainView.updateLogLines();
        this.mainView.tryScrollBottom();
    }

    public List<FindSelect> updateFinder() {
        List<FindSelect> result = new ArrayList<>();
        int i = 0;
        for (Log log : this.logFilted) {
            if (this.finder.isHasCondition()) {
                List<Integer> columns = this.finder.finded(log);
                for (Integer column : columns) {
                    log.addFinded(column);
                    result.add(new FindSelect(column, log));
                }
            } else {
                log.clearFinded();
            }
            i++;
        }
        this.repaint();
        return result;
    }

    public void scrollToRow(int row) {
        Rectangle rect = StyleTable.this.getCellRect(row, 0, true);
        StyleTable.this.scrollRectToVisible(rect);
    }

    public void removeAllItems() {
        while (this.model.getRowCount() > 0) {
            this.model.removeRow(0);
        }
        this.logAll.clear();
        this.logFilted.clear();
    }

    public String toLogString() {
        String result = "";
        for (Log log : this.logAll)
            result += log.getOriginText() + "\r\n";
        return result;
    }

    /**
     * 根据数据内容自动调整列宽。//resize column width automatically
     */
    public void fitTableColumns() {
        this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int rowCount = this.getRowCount();
        JTableHeader header = this.getTableHeader();
        Enumeration columns = this.getColumnModel().getColumns();
        while (columns.hasMoreElements()) {
            TableColumn column = (TableColumn) columns.nextElement();
            int col = header.getColumnModel().getColumnIndex(column.getIdentifier());
            if (rowCount <= 0) {
                columnMaxWidths[col] = (int) header.getDefaultRenderer().getTableCellRendererComponent
                        (this, column.getIdentifier(), false, false, -1, col).getPreferredSize().getWidth();
            } else {
                int nowWidth = columnMaxWidths[col];
                // 遍历最后一行即可
                int preferredWidth = (int) this.getCellRenderer(rowCount - 1, col).getTableCellRendererComponent
                        (this, this.getValueAt(rowCount - 1, col), false, false, rowCount - 1, col).getPreferredSize().getWidth();
                if (preferredWidth > nowWidth) {
                    columnMaxWidths[col] = preferredWidth;
                    header.setResizingColumn(column); // 此行很重要
                    column.setWidth(preferredWidth + this.getIntercellSpacing().width);
                }
            }
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

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public void setFinder(Find finder) {
        this.finder = finder;
    }

    public void updateConfig(int cacheNum) {
        this.maxLogRow = cacheNum;
        this.tryCheckLogOverflow();
    }

    // 检测日志是否已经溢出
    private void tryCheckLogOverflow() {
        while (this.logFilted.size() > this.maxLogRow) {
            this.model.removeRow(0);
            this.logFilted.remove(0);
        }
        while (this.logAll.size() > this.maxLogRow) {
            this.logAll.remove(0);
        }
    }

    public int getAllLines() {
        return this.logAll.size();
    }

    public int getMaxLines() {
        return this.maxLogRow;
    }

    public int getShowLines() {
        return this.logFilted.size();
    }

    /**
     * 定义内部类，用于控制单元格颜色，每两行颜色相间，本类中定义为蓝色和绿色。
     *
     * @author Sidney
     */
    private class RowRenderer extends DefaultTableCellRenderer {

        public Component getTableCellRendererComponent(JTable t, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Log log = StyleTable.this.logFilted.get(row);
            String level = log.getLevel();
            this.setForeground(LogType.getColorByLevel(level));
            // 处理选中状态
            if (log.isFinded(column)) {
                if (log.isFindSelectedColumn(column)) {
                    this.setBackground(Color.MAGENTA);
                } else {
                    this.setBackground(Color.YELLOW);
                }
            } else {
                this.setBackground(Color.WHITE);
            }
            return super.getTableCellRendererComponent(t, value, isSelected,
                    hasFocus, row, column);
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
