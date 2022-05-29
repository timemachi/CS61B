
public class Grandma extends Person {
    public Grandma(String name, int age) {
        super(name, age);
    }

    @Override
    public void greet(Person other) {System.out.println("Hello, young whippersnapper");}
    public void greet(Grandma other) {System.out.println("How was bingo, " + other.name + "?");}
}