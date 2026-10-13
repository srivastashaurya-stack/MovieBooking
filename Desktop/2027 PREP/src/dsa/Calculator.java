package dsa;
import java.util.*;

public class Calculator {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

//        take input from user till user does not press X or x;
        int ans = 0;
        while(true) {
//            take the operator as input
            System.out.println("enter the operator: ");

            char op = in.next().trim().charAt(0);
            if (op == '+' || op == '-' || op == '*' || op == '/' || op == '%') {

//                input two num

                System.out.println("enter two numbers: ");
                int num1 = in.nextInt();
                int num2 = in.nextInt();

                if (op == '+') {
                    ans = num1 + num2;
                }

                if (op == '-') {
                    ans = num1 - num2;
                }

                if (op == '*') {
                    ans = num1 * num2;
                }

                if (op == '/') {
                    if (num2 != 0) {
                        ans = num1 / num2;
                    }
                }

                if (op == '%') {
                    ans = num1 % num2;
                }
            } else if (op == 'x' || op == 'X') {
                break;  /* if the operator is x or X break the while loop */
            } else {
                System.out.println("invalid operation");
            }
            System.out.println(ans);
        }
    }
}
