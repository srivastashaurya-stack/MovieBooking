package dsa;

public class Day2Sorting {
    public static boolean isSorted(int[] arr) {
        for (int i = 0; i < arr.length-1; i++) {
            if(arr[i] > arr[i+1]){
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args){

        int[] arr1= {12,24,35,46,57,68,79};
        int[] arr2= {8,97,86,64,13,45,87};

        System.out.println(isSorted(arr1));
        System.out.println(isSorted(arr2));
    }
}
