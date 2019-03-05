/*******************************************************************************
 * Copyright (c) 2013,2014 Rüdiger Herrmann
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Rüdiger Herrmann - initial API and implementation
 *   Matt Morrissette - allow to use non-static inner IgnoreConditions
 ******************************************************************************/
package eu.captech.digitalization.commons.basic.test;

import eu.captech.digitalization.commons.basic.doc.Preamble;
import org.junit.Assume;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;

@Preamble (
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "02/12/14",
        creationTime = "16:01",
        lastModified = "02/12/14"
)
public class ConditionalIgnoreRule implements MethodRule{

    public Statement apply(Statement base, FrameworkMethod method, Object target){
        Statement result = base;
        if(hasConditionalIgnoreAnnotation(method)) {
            IgnoreCondition condition = getIgnoreCondition(method, target);
            if(condition.isSatisfied()) {
                result = new IgnoreStatement(condition);
            }
        }
        return result;
    }

    private boolean hasConditionalIgnoreAnnotation(FrameworkMethod method){
        return method.getAnnotation(ConditionalIgnore.class) != null;
    }

    private IgnoreCondition getIgnoreCondition(FrameworkMethod method, Object instance){
        ConditionalIgnore annotation = method.getAnnotation(ConditionalIgnore.class);
        return newCondition(annotation, instance);
    }

    private IgnoreCondition newCondition(ConditionalIgnore annotation, Object instance){
        final Class<? extends IgnoreCondition> condition = annotation.condition();
        try {
            if(condition.isMemberClass()) {
                if(Modifier.isStatic(condition.getModifiers())) {
                    return condition.getDeclaredConstructor(new Class<?>[]{}).newInstance();
                }
                else if(instance != null && instance.getClass().isAssignableFrom(condition.getDeclaringClass())) {
                    return condition.getDeclaredConstructor(new Class<?>[]{instance.getClass()}).newInstance(instance);
                }
                throw new IllegalArgumentException("Conditional class: " + condition.getName() + " was an inner member class however it was not declared inside the test case using it. Either make this class a static class (by adding static keyword), standalone class (by declaring it in it's own file) or move it inside the test case using it");
            }
            else {
                return condition.newInstance();
            }
        }
        catch(RuntimeException re) {
            throw re;
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public interface IgnoreCondition{
        boolean isSatisfied();
    }

    @Retention (RetentionPolicy.RUNTIME)
    @Target ({ElementType.METHOD})
    public @interface ConditionalIgnore{
        Class<? extends IgnoreCondition> condition();
    }

    private static class IgnoreStatement extends Statement{
        private IgnoreCondition condition;

        IgnoreStatement(IgnoreCondition condition){
            this.condition = condition;
        }

        @Override
        public void evaluate(){
            Assume.assumeTrue("Ignored by " + condition.getClass().getSimpleName(), false);
        }
    }

}