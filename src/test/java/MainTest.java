import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.lang.reflect.*;


class MainTest {

    static Map<String,Result> getTestResults() throws NoSuchFieldException, IllegalAccessException {
        System.out.println("getTestResults begin");
        Field fInvHandler = Utils.class.getDeclaredField("inv");
        fInvHandler.setAccessible(true);
        System.out.println("fInvHandler CacheHandler=" + fInvHandler.get(null).getClass().toString());

        // получение реального типа объекта CacheHandler, иначе вернётся InvocationHandler
        Field fTestResults = fInvHandler.get(null).getClass().getDeclaredField("results");
        fTestResults.setAccessible(true);
        Map<String,Result> testResults = (Map<String, Result>) fTestResults.get(fInvHandler.get(null));
        System.out.println("testResults=" + testResults.toString());

        System.out.println("getTestResults end");
        return testResults;
    }

    /*
    static String getKey(Method method, int num, int denum ) {
        // определить ключ
        System.out.println("~getKey begin");
        System.out.println("~getKey end");
        return Fraction.class.getName() + ":" + method.getName() + ":" + "num:" + num + ":denum:" + denum ;
    }
    */

    @Test
    void testCacheAll() throws InterruptedException, NoSuchMethodException, NoSuchFieldException, IllegalAccessException {
        Method method = Fraction.class.getMethod("doubleValue");
        Map<String, Result> testResults ;
        String testKey ;
        List<Boolean> bolResults = new ArrayList<>();
        List<Boolean> bolResultsEtalon = new ArrayList<>(
                List.of(true, true)  // время жизни истекло для обоих записей, (num=1,denum=2) и (num=1,denum=4)
        );
        System.out.println("testCacheClear begin");

        int startNum = 1;
        int startDenum = 2;
        Fractionable fr = new Fraction(startNum,startDenum);
        fr = Utils.cache(fr);
        System.out.println("1 call: doubleValue=" + fr.doubleValue());
        System.out.println("2 call: doubleValue=" + fr.doubleValue());

        fr.setDenum(4);

        System.out.println("After setDenum 1 call 1: doubleValue=" + fr.doubleValue());
        System.out.println("After setDenum 1 call 2: doubleValue=" + fr.doubleValue());

        fr.setDenum(startDenum);

        System.out.println("After setDenum 2 call 1: doubleValue=" + fr.doubleValue());
        System.out.println("After setDenum 2 call 2: doubleValue=" + fr.doubleValue());

        Thread.sleep(1500);

        testResults = getTestResults();
        for ( Result r: testResults.values() )
        {
            if (r.isExpired())
                bolResults.add(true);
        }

        System.out.println("After sleep 1 call 1: doubleValue=" + fr.doubleValue());
        System.out.println("After sleep 1 call 2: doubleValue=" + fr.doubleValue());

        Assertions.assertArrayEquals(bolResultsEtalon.toArray(), bolResults.toArray() );
    }


}