package sample;

import checkers.inference.jcrypt.quals.*;

class MatrixMultiplication {

	private static @Sensitive int getInitial(int i) {
		return i++;
	}

	public static void main(String[] args) {
		int m = 2;
		int n = 3;
		int sum = 0;
		int[][] first = new int[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				first[i][j] = getInitial(i);
			}
		}
		int[][] second = new int[n][m];
		int[][] multiply = new int[m][m];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				second[i][j] = getInitial(i);
			}
		}
		for (int c = 0; c < m; c++) {
			for (int d = 0; d < m; d++) {
				for (int k = 0; k < n; k++) {
					sum += first[c][k] * second[k][d];
				}
				multiply[c][d] = sum;
				sum = 0;
			}
		}
		for (int c = 0; c < m; c++) {
			for (int d = 0; d < m; d++) {
				System.out.print(multiply[c][d] + " ");
			}
			System.out.println();
		}
	}
}