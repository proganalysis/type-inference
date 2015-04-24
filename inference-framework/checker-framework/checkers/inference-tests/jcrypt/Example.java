import checkers.inference2.jcrypt.quals.*;

class Example{
        
    public int min_list(int[] list) {
        int min = list[0];
        for(int i = 1; i<list.length; i++) {
            if (list[i] <= min){
                min = list[i];
            }
        }
        return min;
    }
    
    public int id(int element) { return element; }

    public boolean check_ans(int ans) {
        if (ans == 20){
            return true;
        }
        return false;
    }

    public void foo() {
    	/*-@Sensitive*/ int /*@Sensitive*/[] list1 = new int[2];
    	int[] list2 = new int[2];
        /*@Sensitive*/ int x = 9;
        /*@Sensitive*/ int y = 0;
        y++;
        /*@Sensitive*/ int z = 0;
        z += 1;
        list1[0] = x;
        list1[1] = 3;
        list2[0] = 1;
        list2[1] = 7;
    	int min1 = min_list(list1);
    	int min2 = min_list(list2);
    	int ele = id(list1[0]);
        int ans = min1 + min2 + x;
        System.out.println(check_ans(ans));
    }
} 
