public class Person {
    public String name;
    public int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
    public void greet(Person other) {System.out.println("Hello, " + other.name);}
}