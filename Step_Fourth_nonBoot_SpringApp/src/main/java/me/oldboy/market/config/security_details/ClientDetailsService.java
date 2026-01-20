package me.oldboy.market.config.security_details;

import me.oldboy.market.entity.User;
import me.oldboy.market.exceptions.LoginNotFoundException;
import me.oldboy.market.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Класс для загрузки пользовательских данных.
 */
@Service
@EnableJpaRepositories(basePackages = "me.oldboy.market.repository")
public class ClientDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public ClientDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /* Извлекаем из БД клиентов (user-ов) по имени */

    /**
     * Получает пользователя по логину (или имени, в нашем случае email) из базы данных
     *
     * @param login имя пользователя, идентифицирующее пользователя, данные которого требуются.
     * @return основная информация о пользователе
     * @throws LoginNotFoundException, если имя, логин (у нас email) не найдены
     */
    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Optional<User> mayBeUserFound = userRepository.findByEmail(login);
        if (mayBeUserFound.isEmpty()) {
            throw new LoginNotFoundException("User : " + login + " not found!");
        }
        return new SecurityUserDetails(mayBeUserFound.get());
    }
}