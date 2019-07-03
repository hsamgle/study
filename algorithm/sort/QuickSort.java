package ag.sort;

import java.util.Arrays;

/**
 * 快速排序算法
 *
 * @author : huangxianguo@weconex.com
 * @since : 2019-04-19 09:48
 */
public class QuickSort {


    private static final int[] arr = {
            3, 1, 7, 2, 4, 6, 0, 8, 9, 5
    };

    static int times = 0;


    private static void sort(int start, int end) {

        if (start > end) {
            return;
        }

        // 先取第一位为每次开始的基数
        int key = arr[start];

        // 定义左右游标
        int left = start;

        int right = end;

        while (left < right) {

            // 从基数开始，从右往左开始遍历，右游标记录下第一个值小于等于的位置
            while (left < right && arr[right] >= key) {
                right--;
            }

            // 从基数开始，从左往右开始遍历，左游标记录下第一个值大于等于基数的位置
            while (left < right && arr[left] <= key) {
                left++;
            }

            if (left < right) {

                // 游标确定后，两游标所在的值进行交换
                int tmp = arr[left];
                arr[left] = arr[right];
                arr[right] = tmp;

                System.out.println("第 " + (++times) + " 次交换后的结果： " + Arrays.toString(arr));
            }

        }

        // 来到这里说明，左右游标发生了相遇，这个时候需要将基数和当前相遇的位置值进行交换
        arr[start] = arr[left];
        arr[left] = key;

        System.out.println("第 " + (++times) + " 次交换后的结果： " + Arrays.toString(arr));

        // 然后，以当前相遇的地方作为分界线，分为左右两部分再次进行遍历
        sort(start, left - 1);
        sort(left + 1, end);

    }

    public static void main(String[] args) {
        sort(0, arr.length - 1);
    }
}
