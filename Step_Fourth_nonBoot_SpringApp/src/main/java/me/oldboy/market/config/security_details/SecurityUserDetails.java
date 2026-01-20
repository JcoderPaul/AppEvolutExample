package me.oldboy.market.config.security_details;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.oldboy.market.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Класс определяющий UserDetails для Spring Security
 */
@NoArgsConstructor
@AllArgsConstructor
public class SecurityUserDetails implements UserDetails {

    private User user;

    /**
     * Получить список текущих прав пользователей
     *
     * @return коллекция прав пользователей
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().name()));
        return authorities;
    }

    /* Два следующих метода объясняют системе безопасности откуда брать имя/пароль для аутентификации */

    /**
     * Получить логин или имя текущего пользователя
     *
     * @return имя пользователя (логин)
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * Получить текущий пароль пользователя
     *
     * @return пароль пользователя
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Получить текущего пользователя
     *
     * @return сущность пользователя
     */
    public User getUser() {
        return this.user;
    }
}