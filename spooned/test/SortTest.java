package test;



public class SortTest {
    public static void main(java.lang.String[] args) {

    }

    public static int getSecondLargeNumber(int[] nums) {
        if (nums.length < 2) {
            return -1;
        } else {
            util.ArrayUtil1.sortDesc(nums);
        }
        return nums[1];
    }
}