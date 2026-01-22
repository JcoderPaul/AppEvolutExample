package me.oldboy.market.auditor.core.service;

import lombok.extern.slf4j.Slf4j;
import me.oldboy.market.auditor.core.dto.audit.AuditCreateDto;
import me.oldboy.market.auditor.core.dto.audit.AuditReadDto;
import me.oldboy.market.auditor.core.entity.Audit;
import me.oldboy.market.auditor.core.mapper.AuditMapper;
import me.oldboy.market.auditor.core.repository.AuditRepository;
import me.oldboy.market.auditor.core.service.interfaces.AuditService;
import me.oldboy.market.usermanager.core.entity.User;
import me.oldboy.market.usermanager.core.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Реализация сервиса для работы с записями аудита.
 * Содержит простую бизнес-логику валидации перед созданием записей аудита.
 */
@Slf4j
@Transactional(readOnly = true)
public class AuditServiceImpl implements AuditService {
    /**
     * Репозиторий для работы с записями аудита
     */
    private final AuditRepository auditRepository;
    /**
     * Репозиторий для работы с записями о пользователях
     */
    private UserRepository userRepository;

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
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public AuditReadDto create(AuditCreateDto auditCreateDto) {

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