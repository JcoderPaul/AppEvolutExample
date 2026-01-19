package me.oldboy.market.aop.annotations;

import me.oldboy.market.entity.enums.Action;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация помечающая отслеживаемые методы для целей аудита
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Auditable {
     Action operationType();
}
