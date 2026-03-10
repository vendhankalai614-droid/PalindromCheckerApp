import java.util.*;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class PalindromeUseCases {

    public static void main(String[] args) {

        /* UC1 – Application Entry & Welcome Message */
        printHeader("UC1 - Application Entry");

        System.out.println("Palindrome Checker Application Started\n");


        /* UC2 – Hardcoded Palindrome Result */
        printHeader("UC2 - Hardcoded Palindrome Check");

        String hardcoded = "madam";
        System.out.println("Input : " + hardcoded);
        System.out.println("Is Palindrome : " + TwoPointerMethod.check(hardcoded));
        System.out.println();


        /* UC3 – String Reverse Method */
        printHeader("UC3 - Reverse String Method");

        String word1 = "radar";
        System.out.println("Input : " + word1);
        System.out.println("Is Palindrome : " + ReverseStringMethod.check(word1));
        System.out.println();


        /* UC4 – Character Array Method */
        printHeader("UC4 - Character Array Method");

        String word2 = "level";
        System.out.println("Input : " + word2);
        System.out.println("Is Palindrome : " + CharArrayMethod.check(word2));
        System.out.println();


        /* UC5 – Stack Based Method */
        printHeader("UC5 - Stack Based Palindrome");

        String word3 = "noon";
        System.out.println("Input : " + word3);
        System.out.println("Is Palindrome : " + StackMethod.check(word3));
        System.out.println();


        /* UC6 – Queue + Stack Method */
        printHeader("UC6 - Queue + Stack Method");

        String word4 = "civic";
        System.out.println("Input : " + word4);
        System.out.println("Is Palindrome : " + QueueStackMethod.check(word4));
        System.out.println();


        /* UC7 – Deque Method */
        printHeader("UC7 - Deque Based Method");

        String word5 = "refer";
        System.out.println("Input : " + word5);
        System.out.println("Is Palindrome : " + DequeMethod.check(word5));
        System.out.println();


        /* UC8 – LinkedList Method */
        printHeader("UC8 - LinkedList Based Method");

        String word6 = "level";
        System.out.println("Input : " + word6);
        System.out.println("Is Palindrome : " + LinkedListMethod.check(word6));
        System.out.println();


        /* UC9 – Recursive Method */
        printHeader("UC9 - Recursive Palindrome Check");

        String word7 = "racecar";
        System.out.println("Input : " + word7);
        System.out.println("Is Palindrome : " +
                RecursionMethod.check(word7,0,word7.length()-1));
        System.out.println();


        /* UC10 – Case Insensitive & Ignore Spaces */
        printHeader("UC10 - Case Insensitive Palindrome");

        String sentence = "A man a plan a canal Panama";
        String normalized = sentence.replaceAll("[^a-zA-Z0-9]","").toLowerCase();

        System.out.println("Original : " + sentence);
        System.out.println("Normalized : " + normalized);
        System.out.println("Is Palindrome : " + TwoPointerMethod.check(normalized));
        System.out.println();


        /* UC11 – Object Oriented Service */
        printHeader("UC11 - OOP Service Class");

        PalindromeService service = new PalindromeService();

        String word8 = "madam";
        System.out.println("Input : " + word8);
        System.out.println("Is Palindrome : " +
                service.checkPalindrome(word8));
        System.out.println();


        /* UC12 – Strategy Pattern */
        printHeader("UC12 - Strategy Pattern");

        PalindromeStrategy strategy = new StackStrategy();

        String word9 = "noon";
        System.out.println("Input : " + word9);
        System.out.println("Strategy Used : StackStrategy");
        System.out.println("Is Palindrome : " + strategy.check(word9));
        System.out.println();


        /* UC13 – Performance Comparison */
        printHeader("UC13 - Performance Comparison");

        String test = "amanaplanacanalpanama";

        long start = System.nanoTime();
        boolean result = TwoPointerMethod.check(test);
        long end = System.nanoTime();

        System.out.println("Input : " + test);
        System.out.println("Result : " + result);
        System.out.println("Execution Time (nanoseconds) : " + (end-start));
    }


    /* HEADER FUNCTION */
    public static void printHeader(String uc){

        System.out.println("=================================================");
        System.out.println("Welcome to the Palindrome Checker Management System");
        System.out.println("Version : 1.0");
        System.out.println("System initialized successfully.");
        System.out.println("Running : " + uc);
        System.out.println("=================================================");
    }
}


/* UC2 & UC10 – Two Pointer Method */
class TwoPointerMethod {

    public static boolean check(String input){

        int start = 0;
        int end = input.length()-1;

        while(start < end){

            if(input.charAt(start) != input.charAt(end)){
                return false;
            }

            start++;
            end--;
        }

        return true;
    }
}


/* UC3 – Reverse String */
class ReverseStringMethod {

    public static boolean check(String input){

        String reversed = "";

        for(int i=input.length()-1;i>=0;i--){
            reversed += input.charAt(i);
        }

        return input.equals(reversed);
    }
}


/* UC4 – Character Array */
class CharArrayMethod {

    public static boolean check(String input){

        char[] chars = input.toCharArray();

        int start = 0;
        int end = chars.length-1;

        while(start < end){

            if(chars[start] != chars[end]){
                return false;
            }

            start++;
            end--;
        }

        return true;
    }
}


/* UC5 – Stack Method */
class StackMethod {

    public static boolean check(String input){

        Stack<Character> stack = new Stack<>();

        for(char c : input.toCharArray()){
            stack.push(c);
        }

        for(char c : input.toCharArray()){

            if(c != stack.pop()){
                return false;
            }
        }

        return true;
    }
}


/* UC6 – Queue + Stack */
class QueueStackMethod {

    public static boolean check(String input){

        Queue<Character> queue = new LinkedList<>();
        Stack<Character> stack = new Stack<>();

        for(char c : input.toCharArray()){
            queue.add(c);
            stack.push(c);
        }

        while(!queue.isEmpty()){

            if(queue.remove() != stack.pop()){
                return false;
            }
        }

        return true;
    }
}


/* UC7 – Deque */
class DequeMethod {

    public static boolean check(String input){

        Deque<Character> deque = new ArrayDeque<>();

        for(char c : input.toCharArray()){
            deque.addLast(c);
        }

        while(deque.size() > 1){

            if(deque.removeFirst() != deque.removeLast()){
                return false;
            }
        }

        return true;
    }
}


/* UC8 – LinkedList */
class LinkedListMethod {

    public static boolean check(String input){

        LinkedList<Character> list = new LinkedList<>();

        for(char c : input.toCharArray()){
            list.add(c);
        }

        while(list.size() > 1){

            if(list.removeFirst() != list.removeLast()){
                return false;
            }
        }

        return true;
    }
}


/* UC9 – Recursion */
class RecursionMethod {

    public static boolean check(String s,int start,int end){

        if(start >= end){
            return true;
        }

        if(s.charAt(start) != s.charAt(end)){
            return false;
        }

        return check(s,start+1,end-1);
    }
}


/* UC11 – Service Class */
class PalindromeService {

    public boolean checkPalindrome(String input){

        int start = 0;
        int end = input.length()-1;

        while(start < end){

            if(input.charAt(start) != input.charAt(end)){
                return false;
            }

            start++;
            end--;
        }

        return true;
    }
}


/* UC12 – Strategy Pattern */
interface PalindromeStrategy {

    boolean check(String input);
}


class StackStrategy implements PalindromeStrategy {

    public boolean check(String input){

        Stack<Character> stack = new Stack<>();

        for(char c : input.toCharArray()){
            stack.push(c);
        }

        for(char c : input.toCharArray()){

            if(c != stack.pop()){
                return false;
            }
        }

        return true;
    }
}
