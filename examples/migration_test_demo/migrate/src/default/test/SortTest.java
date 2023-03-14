package test;

import util.ArrayUtil1;

public class SortTest {
    public static void main(String[] args) {

    }

    public static int getSecondLargeNumber(int[] nums) {
        if (nums.length < 2) {
            return -1;
        } else {
            ArrayUtil1.sortDesc(nums);
        }
        return nums[1];
    }
}








