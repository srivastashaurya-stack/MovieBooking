package dsa;

public class day1Largestnum {
    public static int getLargest(int[] arr) {
        int max = arr[0];

        for(int i=1; i<arr.length; i++){
            if(arr[i]>max){
                max=arr[i];
            }
        }
        return max;
    }

    public static void main(String[] args){
        int arr[] = {54,94,42,67,90};
        int result = getLargest(arr);
        System.out.println("largest num is " + result);
    }
}
