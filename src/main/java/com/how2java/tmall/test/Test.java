package com.how2java.tmall.test;

public class Test {

    public  void updateX(int value) {
        value = value * 3;
    }

    public  void updateY(Integer value) {
        value = value * 3;
    }

    public static void main(String[] args) {
        int x = 10;
        Integer y = 10;

        Test test = new Test();
        test.updateX(x);
        test.updateY(y);

        System.out.println("x:"+x);
        System.out.println("y:"+y);
    }

}
