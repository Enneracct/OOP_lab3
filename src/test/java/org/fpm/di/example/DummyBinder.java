package org.fpm.di.example;

import org.fpm.di.Binder;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DummyBinder implements Binder {
    private List<Class<?>> classList = new ArrayList<>();
    private List<Class<?>> injectList = new ArrayList<>();
    private List<Class<?>> singletonList = new ArrayList<>();
    private Map<Class<?>, Class<?>> classClassMap = new HashMap<>();
    private Map<Class<?>, Object> singletonMap = new HashMap<>();

    @Override
    public <T> void bind(Class<T> clazz) {
        if(clazz.isAnnotationPresent(Singleton.class)) {
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
            singletonMap.put(clazz, returnedObject);
            return;
        }
        classList.add(clazz);

    }

    @Override
    public <T> void bind(Class<T> clazz, Class<? extends T> implementation) {
        classClassMap.put(clazz,implementation);
    }

    @Override
    public <T> void bind(Class<T> clazz, T instance) {
        singletonMap.put(clazz, instance);
    }

    public List<Class<?>> getClassList() {
        return classList;
    }

    public List<Class<?>> getInjectList() {
        return injectList;
    }

    public List<Class<?>> getSingletonList() {
        return singletonList;
    }

    public Map<Class<?>, Class<?>> getClassClassMap() {
        return classClassMap;
    }

    public Map<Class<?>, Object> getSingletonMap() {
        return singletonMap;
    }
}
