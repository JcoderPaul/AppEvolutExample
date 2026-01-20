package me.oldboy.market.services;

import lombok.extern.slf4j.Slf4j;
import me.oldboy.market.aop.annotations.Loggable;
import me.oldboy.market.dto.audit.AuditCreateDto;
import me.oldboy.market.dto.audit.AuditReadDto;
import me.oldboy.market.entity.Audit;
import me.oldboy.market.entity.User;
import me.oldboy.market.exceptions.ServiceLayerException;
import me.oldboy.market.mapper.AuditMapper;
import me.oldboy.market.repository.AuditRepository;
import me.oldboy.market.repository.UserRepository;
import me.oldboy.market.services.interfaces.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Реализация сервиса для работы с записями аудита.
 * Содержит простую бизнес-логику валидации перед созданием записей аудита.
 *
 * @see AuditService
 * @see AuditRepository
 * @see UserRepository
 * @see Audit
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class AuditServiceImpl implements AuditService {
    /**
     * Репозиторий для работы с записями аудита
     */
    private final AuditRepository auditRepository;
    /**
     * Репозиторий для работы с записями о пользователях
     */
    private final UserRepository userRepository;

    @Autowired
    public AuditServiceImpl(AuditRepository auditRepository, UserRepository userRepository) {
        this.auditRepository = auditRepository;
        this.userRepository = userRepository;
    }

    /**
     * Создает новую запись аудита с предварительной валидацией пользователя.
     * Выполняет проверку, что пользователь с указанным email существует в системе.
     *
     * @param auditCreateDto запись аудита без идентификатора
     * @return созданная запись аудита с присвоенным ей идентификатором
     * @throws ServiceLayerException если пользователь не найден или запись не может быть создана
     */
    @Transactional
    @Override
    @Loggable
    public AuditReadDto create(AuditCreateDto auditCreateDto) throws ServiceLayerException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auditCreateDto.getUserEmail();
        User recordBy;

        if (authentication != null && authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken)) {

            userEmail = authentication.getName();
            recordBy = userRepository.findByEmail(userEmail).get();
        } else if (userEmail != null) {
            Optional<User> mayBeFound = userRepository.findByEmail(userEmail);
            if (mayBeFound.isPresent()) {
                recordBy = mayBeFound.get();
            } else {
                log.warn("Attempting to login with wrong email: " + userEmail);
                return null;
            }
        } else {
            log.warn("Attempting to audit without an authenticated user.");
            return null;
        }

        Audit forRecord = Audit.builder()
                .createAt(auditCreateDto.getCreateAt())
                .createBy(recordBy)
                .action(auditCreateDto.getAction())
                .isSuccess(auditCreateDto.getIsSuccess())
                .auditableRecord(auditCreateDto.getAuditableRecord())
                .build();

        Audit createdAudit = auditRepository.save(forRecord);

        return AuditMapper.INSTANCE.mapToReadDto(createdAudit);
    }

    /**
     * Находит запись аудита по идентификатору.
     *
     * @param entityId идентификатор записи аудита
     * @return найденная запись аудита или null если запись не найдена
     */
    @Override
    @Loggable
    public Optional<AuditReadDto> findById(Long entityId) {
        return auditRepository
                .findById(entityId)
                .map(AuditMapper.INSTANCE::mapToReadDto);
    }

    /**
     * Возвращает все записи аудита из системы.
     *
     * @return список всех записей аудита
     */
    @Override
    @Loggable
    public List<AuditReadDto> findAll() {
        return auditRepository
                .findAll()
                .stream()
                .map(AuditMapper.INSTANCE::mapToReadDto)
                .toList();
    }

    /**
     * Возвращает все аудит-записи из БД по email пользователя "привязанного" к ним.
     *
     * @param userEmail электронный адрес пользователя (в нашей реализации второй уникальный идентификатор)
     * @return список аудит-записей связных с пользователем
     */
    @Override
    @Loggable
    public List<AuditReadDto> findAllAuditsByUserEmail(String userEmail) {
        Optional<List<Audit>> mayBeAuditListFound = auditRepository.findByCreationUserEmail(userEmail);
        if (mayBeAuditListFound.get().size() != 0) {
            return mayBeAuditListFound
                    .get()
                    .stream()
                    .map(AuditMapper.INSTANCE::mapToReadDto)
                    .toList();
        }
        return new ArrayList<>();
    }
}