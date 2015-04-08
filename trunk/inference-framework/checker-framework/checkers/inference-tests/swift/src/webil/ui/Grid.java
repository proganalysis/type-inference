package webil.ui;

public class Grid extends Panel
{
    public Grid(int row, int column) {
	super();
        initWidget(row, column);
    }

    public Grid(int row, int column, String id) {
	super(id);
        initWidget(row, column);
    }
 
    protected void initWidget() {
        this.widgetImpl = new GridImpl(this);
    }

    protected void initWidget(int row, int column) {
        this.widgetImpl = new GridImpl(this, row, column);
    }
   
    public void setWidget(int row, int column, Widget w) {
        GridImpl grid = (GridImpl)this.widgetImpl;
        grid.setWidget(row, column, w.widgetImpl);
    }
    
    public boolean clearCell(int row, int col) {
        return ((GridImpl) widgetImpl).clearCell(row, col);
    }
    
    public int getCellCount(int row) {
        return ((GridImpl) widgetImpl).getCellCount(row);
    }
    
    public int getColumnCount() {
        return ((GridImpl) widgetImpl).getColumnCount();
    }
    
    public int getRowCount() {
        return ((GridImpl) widgetImpl).getRowCount();
    }
    
    public void resize(int rows, int columns) {
        ((GridImpl) widgetImpl).resize(rows, columns);
    }
    
    public void resizeColumns(int columns) {
        ((GridImpl) widgetImpl).resizeColumns(columns);
    }
    
    public void resizeRows(int rows) {
        ((GridImpl) widgetImpl).resizeRows(rows);
    }

}
