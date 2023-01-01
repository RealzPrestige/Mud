package dev.zprestige.mud.util.impl;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ClassFinder {

    public static ArrayList<Class<?>> classesExtending(String folder, Class<?> extendingClass) {
        try {
            return ReflectionUtil.getClassesForPackage("dev.zprestige.mud.module." + folder).stream().filter(extendingClass::isAssignableFrom).collect(Collectors.toCollection(ArrayList::new));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}