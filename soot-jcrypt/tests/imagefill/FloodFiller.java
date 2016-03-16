package imagefill;
/** This class, which does flood filling, is used by the floodFill() macro function and
    by the particle analyzer
    The Wikipedia at "http://en.wikipedia.org/wiki/Flood_fill" has a good 
    description of the algorithm used here as well as examples in C and Java. 
*/
public class FloodFiller {
    int maxStackSize = 500; // will be increased as needed
    int[] xstack = new int[maxStackSize];
    int[] ystack = new int[maxStackSize];
    int stackSize;
    Integer[][] pixels;
	int targetColor;
  
    public FloodFiller(Integer[][] pixels, int targetColor) {
    	this.pixels = pixels;
		this.targetColor = targetColor;
    }

	public int getPix(int ax, int ay) {
		int x = ax;
		int y = ay;
		if (x < 0 || x >= pixels.length || y < 0 || y >= pixels[0].length)
			return -1;
		else
			return pixels[x][y];
	}
	public void setPix(int ax, int ay, int c) {
		int x = ax;
		int y = ay;
		if (x < 0 || x >= pixels.length || y < 0 || y >= pixels[0].length)
			return;
		else
			pixels[x][y] = c;
	}

    /** Does a 4-connected flood fill using the current fill/draw
        value, which is defined by ImageProcessor.setValue(). */
    public boolean fill(int x, int y) {
        int width = pixels.length;
        int height = pixels[0].length;
        int color = getPix(x, y);
        fillLine(x, x, y);
        int newColor = getPix(x, y);
		setPix(x, y, color);
        if (color==newColor) return false;
        stackSize = 0;
        push(x, y);
        while(true) {   
            x = popx(); 
            if (x == -1) return true;
            y = popy();
            if (getPix(x, y)!=color) continue;
            int x1 = x; int x2 = x;
            while (getPix(x1, y)==color && x1>=0) x1--; // find start of scan-line
            x1++;
            while (getPix(x2, y)==color && x2<width) x2++;  // find end of scan-line                 
            x2--;
            fillLine(x1,x2,y); // fill scan-line
            boolean inScanLine = false;
            for (int i=x1; i<=x2; i++) { // find scan-lines above this one
                if (!inScanLine && y>0 && getPix(i, y-1)==color)
                    {push(i, y-1); inScanLine = true;}
                else if (inScanLine && y>0 && getPix(i, y-1)!=color)
                    inScanLine = false;
            }
            inScanLine = false;
            for (int i=x1; i<=x2; i++) { // find scan-lines below this one
                if (!inScanLine && y<height-1 && getPix(i, y+1)==color)
                    {push(i, y+1); inScanLine = true;}
                else if (inScanLine && y<height-1 && getPix(i, y+1)!=color)
                    inScanLine = false;
            }
        }        
    }
    
    final void push(int x, int y) {
        stackSize++;
        if (stackSize==maxStackSize) {
            int[] newXStack = new int[maxStackSize*2];
            int[] newYStack = new int[maxStackSize*2];
            System.arraycopy(xstack, 0, newXStack, 0, maxStackSize);
            System.arraycopy(ystack, 0, newYStack, 0, maxStackSize);
            xstack = newXStack;
            ystack = newYStack;
            maxStackSize *= 2;
        }
        xstack[stackSize-1] = x;
        ystack[stackSize-1] = y;
    }
    
    final int popx() {
        if (stackSize==0)
            return -1;
        else
            return xstack[stackSize-1];
    }

    final int popy() {
        int value = ystack[stackSize-1];
        stackSize--;
        return value;
    }

    final void fillLine(int x1, int x2, int y) {
        if (x1>x2) {int t = x1; x1=x2; x2=t;}
        for (int x=x1; x<=x2; x++)
			setPix(x, y, targetColor);
    }

}
