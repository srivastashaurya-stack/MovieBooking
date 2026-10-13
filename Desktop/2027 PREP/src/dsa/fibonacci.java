package dsa;
import java.util.*;

public class fibonacci {
    public static void main(String[] args) {
        Scanner in= new Scanner(System.in);

        int n = in.nextInt();
        int a = 0;
        int b = 1;
        int count = 2; /* Counter jo track karta hai ki hum kaunse index tak calculate kar chuke hain. */

        while(count <= n){
            int temp = b; /* temp: Purani b ki value ko temporarily store karta hai taaki a ko update karte waqt b ki nayi value overwrite na ho */
            b = b+a;
            a = temp;
            count++;
        }
        System.out.println(b);
    }
}
