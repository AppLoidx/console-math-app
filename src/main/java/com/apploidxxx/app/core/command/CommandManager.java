package com.apploidxxx.app.core.command;

import com.apploidxxx.app.core.command.stereotype.Executable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Arthur Kupriyanov on 18.02.2020
 */
public class CommandManager {
    private final Map<List<String>, Command> commandMap = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(CommandManager.class);

    public Command getCommand(String name){
        return findByName(name);
    }

    private Command findByName(String name){
        for (List<String> keys : commandMap.keySet()){
            if (keys.contains(name)) return commandMap.get(keys);
        }

        return (console, context) ->  console.println("Command not found");
    }

    public void init(String commandsPackage) {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(true);

        scanner.addIncludeFilter(new AnnotationTypeFilter(Executable.class));

        for (BeanDefinition bd : scanner.findCandidateComponents(commandsPackage)){
            try {
                processBean(bd);
            } catch (ClassNotFoundException e){
                logger.error("Command class not found", e);
            }
        }

    }

    private void processBean(BeanDefinition bd) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(bd.getBeanClassName());

        if (!isImplementedInterface(clazz)) {
            logger.warn(String.format("Command %s not implemented %s", clazz.getSimpleName(), Command.class.getSimpleName()));
            return;
        }

        for (Constructor<?> c : clazz.getDeclaredConstructors()) {
            Constructor<Command> castedConstructor = (Constructor<Command>) c;
            Command commandInstance = null;
            castedConstructor.setAccessible(true);
            if (castedConstructor.getParameterTypes().length == 0) {
                try {
                    commandInstance = castedConstructor.newInstance();
                    commandMap.put(getCommandNames(clazz), commandInstance);
                    logger.info("Added command: " + bd.getBeanClassName());
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private List<String> getCommandNames(Class<?> clazz) {

        Executable executable = clazz.getAnnotation(Executable.class);
        if (executable == null) {
            logger.warn(Executable.class.getSimpleName() + " annotation not found for " + clazz.getSimpleName());
            return new ArrayList<>();
        }
        List<String> names = new ArrayList<>(List.of(executable.aliases()));
        names.add(executable.value());
        return names;

    }

    private boolean isImplementedInterface(Class<?> clazz) {
        boolean implemented = false;
        for (Class<?> i : clazz.getInterfaces()) {
            System.out.println(i.getSimpleName());
            if (i == Command.class) {
                implemented = true;
                break;
            }
        }
        return implemented;
    }
}
