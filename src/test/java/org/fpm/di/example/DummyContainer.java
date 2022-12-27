package org.fpm.di.example;

import org.fpm.di.Container;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DummyContainer implements Container {

    private List<Class<?>> classList = new ArrayList<>();
    private List<Class<?>> injectList = new ArrayList<>();
    private List<Class<?>> singletonList = new ArrayList<>();
    private Map<Class<?>, Class<?>> classClassMap = new HashMap<>();
    private Map<Class<?>, Object> singletonMap = new HashMap<>();

    public DummyContainer(DummyBinder dummyBinder) {
        classList = dummyBinder.getClassList();
        injectList = dummyBinder.getInjectList();
        singletonMap = dummyBinder.getSingletonMap();
        classClassMap = dummyBinder.getClassClassMap();
        singletonMap = dummyBinder.getSingletonMap();
    }

    @Override
    public <T> T getComponent(Class<T> clazz) {
        if (classList.contains(clazz)){
            T returnedT;
            if ((returnedT = checkInjection(clazz))!=null)
                return returnedT;
            Constructor<T> constructor;
            try {
                constructor = clazz.getConstructor();
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            T returnedObject;
            try {
                returnedObject = constructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            return returnedObject;
        }
        if(singletonMap.containsKey(clazz))
            return (T) singletonMap.get(clazz);
        if(classClassMap.containsKey(clazz))
            return (T) getComponent(classClassMap.get(clazz));

        return null;
    }

    private <T> T checkInjection(Class<T> tClass){
        for (Constructor<?> tConstructor: tClass.getConstructors()){
            if(tConstructor.isAnnotationPresent(Inject.class)){
                return createInjectObject(tConstructor);
            }
        }
        return null;
    }

    private <T> T createInjectObject(Constructor<?> tConstructor) {
        ArrayList<Object> objects = new ArrayList<>();
        for (Class<?> clazz: tConstructor.getParameterTypes()){
            objects.add(getComponent(clazz));
        }
        try {
            return (T) tConstructor.newInstance(objects.toArray());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
