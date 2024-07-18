import java.lang.reflect.*;
import java.sql.SQLOutput;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CachingHandler<T> implements InvocationHandler {
    private T currentObject;
    private Map<String,Result> results = new HashMap<>();

    public CachingHandler(T currentObject){
        this.currentObject = currentObject;
    }

    private class CacheCleaner extends Thread {
        @Override
        public void run() {
            System.out.println("Поток CacheCleaner запущен!");

            //копируем кэш для очистки
            ConcurrentHashMap<String,Result> clearResults = new ConcurrentHashMap<>(results);
            //вспомогательный кэш на время очистки
            ConcurrentHashMap<String,Result> tempResults = new ConcurrentHashMap<>();
            results = tempResults;

            if (clearResults.isEmpty()) return ;

            for ( String key: clearResults.keySet() )
            {
                if ( clearResults.get(key).isExpired() )
                {
                    clearResults.remove(key);
                    System.out.println("CacheCleaner :: запись из кэша удалена!");
                }
            }

            ConcurrentHashMap<String,Result> joinResults = new ConcurrentHashMap<>(results);
            joinResults.putAll(clearResults);
            results = joinResults;

        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("currentObject=" + currentObject.toString());

        Result res;
        Method currentMethod;

        System.out.println(this.getClass().toString() + "::" + "invoke begin" );
        currentMethod = currentObject.getClass().getMethod(method.getName(), method.getParameterTypes());

        // определить ключ
        List <CharSequence> lstKeyFieldVals = new ArrayList<>() ;
        String stClassMethod = currentObject.getClass().getName() + ":" + method.getName();
        lstKeyFieldVals.add(stClassMethod);
        Field[] fields = currentObject.getClass().getDeclaredFields();
        for(Field field: fields){
            field.setAccessible(true);
            if (field.get(currentObject) != null){
                lstKeyFieldVals.add(field.getName());
                lstKeyFieldVals.add(field.get(currentObject).toString());
            }
        }
        String newKey = String.join(":", lstKeyFieldVals);
        System.out.println("newKey=" + newKey);

        res = results.get(newKey);

        if (currentMethod.isAnnotationPresent(Cache.class)) {
            long lifetime = currentMethod.getAnnotation(Cache.class).time();

            if (res == null || res.isExpired() ) {
                System.out.println("cache miss" );
                //return results.get(newKey);
                res = new Result(System.currentTimeMillis() + lifetime, (Double)method.invoke(currentObject,args) );
                if (lifetime == 0) res.tmLife = 0L;
                results.put(newKey, res);
            }
            else {
                //res = method.invoke(currentObject, args);
                res.tmLife = System.currentTimeMillis() + lifetime ;
                if (lifetime == 0) res.tmLife = 0L;
                results.put(newKey, res);
                System.out.println("cache hit");
                //return res;
            }

            // условие запуска очистки кэша
            int activeRec = 0 ;
            int expiredRec = 0;
            float correl = 0f ;
            for ( Result r: results.values() )
            {
                if (System.currentTimeMillis() > r.tmLife) expiredRec++ ;
                else activeRec++;
            }

            correl = ((float)expiredRec) / (activeRec + expiredRec);
            if (correl>0.5) {
                System.out.println("CacheCleaner start!");
                new CacheCleaner().start();
            }

            return res.value;

        }

        if (currentMethod.isAnnotationPresent(Mutator.class)) {
            // System.out.println("cache clear" );
            System.out.println("Mutator" );
            //results.clear();
        }

        return method.invoke(currentObject, args);


    }


}
