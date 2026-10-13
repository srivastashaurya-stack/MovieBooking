package dsa;
import java.util.*;

public class day4TwoSum {
    public static int[] TwoSum(int[] nums, int target){

        for(int i = 0; i<nums.length; i++){
            for(int j = 1; j<nums.length; j++){
                if(nums[i] + nums[j] == target){
                    return new int[]{i,j};
                }
            }
        }
        return new int[]{};
    }

    public static void main(String[] args){
         int[] nums={1,2,3,4,5,6,7,8,9};
         int target = 6;

         int[] ans = TwoSum(nums, target);

        System.out.println(Arrays.toString(ans));
    }
}
