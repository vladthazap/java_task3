import java.lang.annotation.Annotation;
import java.lang.reflect.*;

public class Utils {
    private static InvocationHandler inv ;

    @SuppressWarnings("unchecked")
    public static <T> T cache(T objectIncome)
    {
        inv = new CachingHandler(objectIncome);
        /*
        return (T) Proxy.newProxyInstance(objectIncome.getClass().getClassLoader(),
                objectIncome.getClass().getInterfaces(),
                new CachingHandler(objectIncome));
         */
        return (T) Proxy.newProxyInstance(objectIncome.getClass().getClassLoader(),
                objectIncome.getClass().getInterfaces(),
                inv);

    }
}
