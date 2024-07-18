import java.lang.reflect.*;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        System.out.println("Main begin");
        Fractionable fr = new Fraction(1,2);
        fr = Utils.cache(fr);

        System.out.println("1 call: doubleValue=" + fr.doubleValue());
        System.out.println("2 call: doubleValue=" + fr.doubleValue());
        fr.setDenum(4);
        System.out.println("After setDenum 1 call 1: doubleValue=" + fr.doubleValue());
        System.out.println("After setDenum 1 call 2: doubleValue=" + fr.doubleValue());
        fr.setDenum(2);
        System.out.println("After setDenum 2 call 1: doubleValue=" + fr.doubleValue());
        System.out.println("After setDenum 2 call 2: doubleValue=" + fr.doubleValue());

        System.out.println("Thread.sleep");
        Thread.sleep(3000);

        System.out.println("After sleep 1 call 1: doubleValue=" + fr.doubleValue());
        System.out.println("After sleep 1 call 2: doubleValue=" + fr.doubleValue());

    }


}
