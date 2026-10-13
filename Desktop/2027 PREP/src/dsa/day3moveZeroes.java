package dsa;

public class day3moveZeroes {
    public static void moveZeroes(int[] nums){
        int count = 0;

        for(int i = 0; i<nums.length; i++){
            if(nums[i] != 0){
                nums[count++] = nums[i];
            }
        }
        while(count<nums.length){
            nums[count++] = 0;
        }
    }

    public static void main(String[] args){
        int[] nums = {0,2,3,4,0,6,0,5};

        moveZeroes(nums);
        for (int i = 0; i < nums.length; i++) {
            System.out.print(nums[i] + " ");
        }
    }
}
