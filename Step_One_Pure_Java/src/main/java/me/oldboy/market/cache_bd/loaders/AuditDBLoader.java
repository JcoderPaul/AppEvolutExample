package me.oldboy.market.cache_bd.loaders;

import lombok.AllArgsConstructor;
import me.oldboy.market.cache_bd.AuditDB;
import me.oldboy.market.cache_bd.ProductDB;
import me.oldboy.market.entity.Audit;
import me.oldboy.market.entity.log_enum.Action;
import me.oldboy.market.entity.log_enum.Status;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Класс загружающий данные в кэш "таблицу" аудита
 */
@AllArgsConstructor
public class AuditDBLoader {
    /**
     * Метод инициализирующий процесс загрузки данных в "кэш" из файла
     *
     * @param auditDB кэш БД для загрузки данных по аудиту
     */
    public static void initInMemoryBase(AuditDB auditDB) {
        List<Audit> readRecords = readFromExternalFile();
        if (readRecords != null) {
            for (int i = 0; i < readRecords.size(); i++) {
                auditDB.add(readRecords.get(i));
            }
        }
    }

    public static void writeToExternalFile(AuditDB auditDB) {
        List<Audit> allAudRecord = auditDB.getAuditLogList();
        String path = "db_files/audit.sr";

        File file = new File(path);
        if (!file.isFile() || !file.exists()) {
            System.out.println("Файл БД не существует и будет создан");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file))) {
            outputStream.writeObject(allAudRecord);
            System.out.println("Добавлены новые аудит записи в файл");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Audit> readFromExternalFile() {
        List<Audit> allAudRecord = new ArrayList<>();
        String path = "db_files/audit.sr";

        File file = new File(path);
        if (!file.isFile() || !file.exists() || file.length() == 0) {
            System.out.println("Файл БД не существует или пуст");
            return allAudRecord;
        }

        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file))) {
            allAudRecord = (List<Audit>) objectInputStream.readObject();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return allAudRecord;
    }

    /**
     * Метод инициализирующий процесс загрузки тестовых данных в "кэш"
     *
     * @param auditDB кэш БД для загрузки данных по аудиту
     */
    public static void initTestInMemoryBase(AuditDB auditDB, ProductDB productDB) {
        Audit rec_1 = Audit.builder()
                .timestamp(new Date().getTime())
                .userEmail("u1@market.ru")
                .action(Action.ADD_PRODUCT)
                .isSuccess(Status.SUCCESS)
                .product(productDB.findProductById(1L).get())
                .build();

        Audit rec_2 = Audit.builder()
                .timestamp(new Date().getTime())
                .userEmail("u1@market.ru")
                .action(Action.UPDATE_PRODUCT)
                .isSuccess(Status.FAIL)
                .product(productDB.findProductById(2L).get())
                .build();

        auditDB.add(rec_1);
        auditDB.add(rec_2);
    }
}