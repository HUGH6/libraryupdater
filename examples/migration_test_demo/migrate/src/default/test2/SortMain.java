package test2;

public class SortMain {
    public static int getSecondLargeNumber(int[] nums) {
        if (nums.length < 2) {
            return -1;
        } else {
            util.ArrayUtil.sortDesc(nums);
        }
        return nums[1];
    }

    public static void main(String[] args) {
        int [] arr = new int[]{3,5,7,1,2};
        System.out.println(getSecondLargeNumber(arr));
    }
}
