import checkers.inference2.jcrypt.quals.*;

class Example{
    sen static final int[] list1 = new int[2];
    sen static final int[] list2 = new int[2];
    sen static final int x = 3;

    poly list1[0] = 9;
    poly list1[1] = 3;
    poly list2[0] = 1;
    poly list2[1] = 7;
    
    static int min_list(int[] list){
        int min = list[0];
        for(int i = 1; i<list.length; i++){
            if (list[i] <= min){
                min = list[i];
            }
        }
        return min;
    }

    static boolean check_ans(int ans){
        if (ans == 20){
            return true;
        }
        return false;
    }

    public static void main(String []args){
    	int min1 = min_list(list1);
	int min2 = min_list(list2);
        int ans = min1 + min2 + x;
        ans = 2 * ans;
        System.out.println(check_ans(ans));
    }
} 
