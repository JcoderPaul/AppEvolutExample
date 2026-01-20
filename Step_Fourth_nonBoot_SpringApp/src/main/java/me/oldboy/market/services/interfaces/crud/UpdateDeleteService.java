package me.oldboy.market.services.interfaces.crud;

/**
 * Полнофункциональный сервис для операций Create, Read, Update, Delete над сущностями.
 * Объединяет функционал чтения и создания через множественное наследование.
 *
 * @param <Key> тип уникального идентификатора сущности
 * @param <UpdateDto>> тип управляемой сущности
 * @see ReadOnlyService
 * @see CreateOnlyService
 */
public interface UpdateDeleteService<Key, UpdateDto, ReadDto>  {
    /**
     * Обновляет данные существующей сущности.
     *
     * @param updateEntityWithIdAndData сущность с обновленными данными и идентификатором
     * @return обновленную сущность
     */
    ReadDto update(UpdateDto updateEntityWithIdAndData);

    /**
     * Удаляет сущность по идентификатору.
     *
     * @param entityId идентификатор сущности для удаления
     * @return true - удаление успешно, false - в противном случае
     */
    boolean delete(Key entityId);
}
