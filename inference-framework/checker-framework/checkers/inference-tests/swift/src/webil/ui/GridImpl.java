package webil.ui;

import webil.signature.Client;

public class GridImpl extends PanelImpl implements Client
{
    public GridImpl(Object wilWidget, int row, int column) {
        super(wilWidget);
        initGWTWidget(row, column);
    }
    public GridImpl(Object wilWidget) { 
        super(wilWidget);
    }

 
    protected void initGWTWidget() {
        this.gwtWidget = new com.google.gwt.user.client.ui.Grid();
    }

    protected void initGWTWidget(int row, int column) {
        this.gwtWidget = new com.google.gwt.user.client.ui.Grid(row, column);
    }
   
    public void setWidget(int row, int column, WidgetImpl w) {
	com.google.gwt.user.client.ui.Grid grid = 
	    (com.google.gwt.user.client.ui.Grid) this.gwtWidget;
	grid.setWidget(row, column, w.gwtWidget);
    }
    
    public boolean clearCell(int row, int col) {
        return ((com.google.gwt.user.client.ui.Grid) gwtWidget).clearCell(row, col);
    }
    
    public int getCellCount(int row) {
        return ((com.google.gwt.user.client.ui.Grid) gwtWidget).getCellCount(row);
    }
    
    public int getColumnCount() {
        return ((com.google.gwt.user.client.ui.Grid) gwtWidget).getColumnCount();
    }
    
    public int getRowCount() {
        return ((com.google.gwt.user.client.ui.Grid) gwtWidget).getRowCount();
    }
    
    public void resize(int rows, int columns) {
        ((com.google.gwt.user.client.ui.Grid) gwtWidget).resize(rows, columns);
    }
    
    public void resizeColumns(int columns) {
        ((com.google.gwt.user.client.ui.Grid) gwtWidget).resizeColumns(columns);
    }
    
    public void resizeRows(int rows) {
        ((com.google.gwt.user.client.ui.Grid) gwtWidget).resizeRows(rows);
    }

    // FIXME: how to wrap a gwt widget?
    /*public Widget getWidget(int row, int column) {
	com.google.gwt.user.client.ui.Grid grid = 
	    (com.google.gwt.user.client.ui.Grid) this.widget;
	return grid.getWidget(row, column);
    }*/

}
