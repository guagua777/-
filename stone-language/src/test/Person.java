package test;

public class Person {
    public void greet() {
        System.out.println("Hi!");
    }
    public static void main(String[] args) {
        new Person().greet();
    }
}