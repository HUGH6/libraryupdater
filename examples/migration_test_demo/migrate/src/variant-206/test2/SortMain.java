package test2;
public class SortMain {

	public static int getSecondLargeNumber(int[] nums) {
		if (nums.length < 2) {
			return -1;
		} else {
			try {
				boolean $boolean_var0 = false;
				shadow.util.ArrayUtilV2.sort(true, nums);}
			catch (IllegalArgumentException e) {}}
		return nums[1];
	}

	public static void main(java.lang.String[] args) {
		int[] arr = new int[]{ 3, 5, 7, 1, 2 };
		java.lang.System.out.println(test2.SortMain.getSecondLargeNumber(arr));
	}}