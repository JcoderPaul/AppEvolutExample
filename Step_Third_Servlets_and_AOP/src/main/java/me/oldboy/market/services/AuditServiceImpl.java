package me.oldboy.market.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.oldboy.market.entity.Audit;
import me.oldboy.market.exceptions.ServiceLayerException;
import me.oldboy.market.repository.AuditRepositoryImpl;
import me.oldboy.market.repository.UserRepositoryImpl;
import me.oldboy.market.repository.interfaces.AuditRepository;
import me.oldboy.market.repository.interfaces.UserRepository;
import me.oldboy.market.services.interfaces.AuditService;

import java.util.List;

/**
 * Реализация сервиса для работы с записями аудита.
 * Содержит простую бизнес-логику валидации перед созданием записей аудита.
 *
 * @see AuditService
 * @see AuditRepositoryImpl
 * @see UserRepositoryImpl
 * @see Audit
 */
@Slf4j
@AllArgsConstructor
public class AuditServiceImpl implements AuditService {
    /**
     * Репозиторий для работы с записями аудита
     */
    private AuditRepository auditRepository;
    /**
     * Репозиторий для работы с записями о пользователях
     */
    private UserRepository userRepository;

    /**
     * Создает новую запись аудита с предварительной валидацией пользователя.
     * Выполняет проверку, что пользователь с указанным email существует в системе.
     *
     * @param entityWithoutId запись аудита без идентификатора
     * @return созданная запись аудита с присвоенным ей идентификатором
     * @throws ServiceLayerException если пользователь не найден или запись не может быть создана
     */
    @Override
    public Audit create(Audit entityWithoutId) throws ServiceLayerException {
        if (userRepository.findByEmail(entityWithoutId.getCreateBy()).isEmpty()) {
            log.warn("Попытка входа в систему с email: " + entityWithoutId.getCreateBy());
            return null;
        }
        return auditRepository
                .create(entityWithoutId)
                .orElseThrow(() -> new ServiceLayerException("Аудит текущего действия - " + entityWithoutId.getAction() + " невозможен"));
    }

    /**
     * Находит запись аудита по идентификатору.
     *
     * @param entityId идентификатор записи аудита
     * @return найденная запись аудита или null если запись не найдена
     */
    @Override
    public Audit findById(Long entityId) {
        return auditRepository
                .findById(entityId)
                .orElse(null);
    }

    /**
     * Возвращает все записи аудита из системы.
     *
     * @return список всех записей аудита
     */
    @Override
    public List<Audit> findAll() {
        return auditRepository.findAll();
    }
}