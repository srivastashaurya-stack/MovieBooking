package dsa;

public class Day1ReverseArray {
    public static void ReverseArray(int[] arr){
        int start = 0;
        int end = arr.length-1;

        while(start<end) {
            int temp = arr[start];
            arr[start] = arr[end];
            arr[end] = temp;

            start++;
            end--;
        }
    }

    public static void main(String[] args){
        int[]arr = {76,13,24,35,46,57};

        System.out.println("original ");
        for(int i = 0; i<arr.length; i++){
            System.out.print(arr[i]+ " ");
        }
        ReverseArray(arr);

        System.out.println("\nreversed ");
        for(int i =0; i<arr.length; i++){
            System.out.print(arr[i] + " ");
        }
    }
}
