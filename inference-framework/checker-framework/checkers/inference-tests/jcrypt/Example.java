package example;

import checkers.inference2.jcrypt.quals.*;

class Example {

	public int min_list(int[] list) {
		int min = list[0];
		for (int i = 1; i < list.length; i++) {
			if (list[i] <= min) {
				min = list[i];
			}
		}
		return min;
	}

	public int id(int element) {
		return element;
	}

	public boolean check_ans(int ans) {
		if (ans == 20) {
			return true;
		}
		return false;
	}

	public void foo() {
		int /*@Sensitive*/ [] list1 = new int[2];
		int[] list2 = new int[2];
		/*@Sensitive*/ int x = 9;
		list1[0] = x;
		list1[1] = x;
		list2[0] = 1;
		list2[1] = 7;
		int min1 = min_list(list1);
		int min2 = min_list(list2);
		int ele1 = id(list1[0]);
		int ele2 = id(list2[0]);
		int ans1 = min1 + ele1;
		int ans2 = min2 + ele2;
		check_ans(ans1);
		check_ans(ans2);
	}

}
