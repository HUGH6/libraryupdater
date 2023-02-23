package test;

public class Demo {
    private static String name = "demo";
    public String name2 = "instance demo";
    private static String name3 = "demo";

    public Demo() {

    }

    public boolean testFunction() {
        String a = "2";
        boolean res = test2(a);
        boolean res = test2(getN());
        boolean res = test2("3" + "3");

        Demo d = new Demo();

        int count = 1 + 1;

        Demo.name3 = "demo3";

        System.out.println(test2(a));
        System.out.println(res);
        System.out.println(count);
        System.out.println(Demo.name);
        System.out.println(d.name2);
    }

    private boolean test2(String a) {
        return a.equals("1");
    }

    private String getN() {
        return "ddd";
    }
}