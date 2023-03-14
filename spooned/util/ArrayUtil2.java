package util;



public class ArrayUtil2 {
    public static void sort(boolean desc, int[] nums) throws java.lang.IllegalArgumentException {
        if (nums == null) {
            throw new java.lang.IllegalArgumentException("illegal array to sort, nums array can not be null");
        }

        java.util.Arrays.sort(nums);
        if (desc) {
            for (int idx = 0; idx < (nums.length / 2); idx++) {
                int tmp = nums[idx];
                nums[idx] = nums[(nums.length - 1) - idx];
                nums[(nums.length - 1) - idx] = tmp;
            }
        }
    }
}