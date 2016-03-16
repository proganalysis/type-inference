package imagefill;

import java.io.*;
import checkers.inference.jcrypt.quals.*;

public class FFTest {
	static Integer[][] image;
	static final int WIDTH = 100;
	static final int HEIGHT = 100;

    private static @Sensitive int asciiNum(int c) {
        return c - 48;
    }
	
	public static void main(String [] argv) throws Exception {
		int width = new Integer(argv[0]);
		int height = new Integer(argv[1]);
		String filename = argv[2];
		
		image = new Integer[width][height];
		
		FileReader reader = new FileReader(filename);
		int c;
		int x = 0;
		int y = 0;
		while ((c = reader.read()) != -1) {
			if (c == 10) {
				y++;
				x = 0;
			} else {
				image[x][y] = asciiNum(c);
				x++;
			}
        }
		reader.close();
		int color = getInitial();
		long start = System.currentTimeMillis();
		FloodFiller ff = new FloodFiller(image, color);
		ff.fill(0, 0);
		System.out.println("Running time " + (System.currentTimeMillis() - start)/1000.0);
		for (y = 0; y < height; ++y) {
			for (x = 0; x < width; ++x) {
				System.out.print(image[x][y]);
				System.out.print(" ");
			}
			System.out.println();
		}
	}
	
	private static @Sensitive int getInitial() {
		return 2;
	}
}
