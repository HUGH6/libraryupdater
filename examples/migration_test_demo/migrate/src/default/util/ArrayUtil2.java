package util;

import java.util.Arrays;

public class ArrayUtil2 {
    public static void sort(boolean desc, int[] nums) throws IllegalArgumentException {
        if (nums == null) {
            throw new IllegalArgumentException("illegal array to sort, nums array can not be null");
        }

        Arrays.sort(nums);
        if (desc) {
            for (int idx = 0; idx < nums.length / 2; idx++) {
                int tmp = nums[idx];
                nums[idx] = nums[nums.length - 1 - idx];
                nums[nums.length - 1 - idx] = tmp;
            }
        }
    }
}


