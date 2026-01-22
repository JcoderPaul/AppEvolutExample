package me.oldboy.market.logger.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация помечающая методы и классы для проведения процедуры логирования (в нашем случае "замер скорости").
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,
        ElementType.TYPE})
public @interface EnableLog {
}
