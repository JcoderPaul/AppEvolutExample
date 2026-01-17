package me.oldboy.market.cache_bd.loaders;

import me.oldboy.market.cache_bd.BrandDB;
import me.oldboy.market.cache_bd.CategoryDB;
import me.oldboy.market.cache_bd.ProductDB;
import me.oldboy.market.entity.Product;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Класс загружающий данные в кэш "таблицу" хранящую данные о товарах
 */
public class ProductBDLoader {
    /**
     * Метод инициализирующий процесс загрузки тестовых данных в "кэш"
     *
     * @param productDB  кэш БД для загрузки данных по доступным товарам
     */
    public static void initInMemoryBase(ProductDB productDB) {
        List<Product> readProductRecords = readFromExternalFile();
        if (readProductRecords != null) {
            for (int i = 0; i < readProductRecords.size(); i++) {
                productDB.add(readProductRecords.get(i));
            }
        }
    }

    public static void writeToExternalFile(ProductDB productDB) {
        List<Product> allProduct = productDB.getProductsList();
        String path = "db_files/product.sr";

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
            outputStream.writeObject(allProduct);
            System.out.println("Файл данных по продуктам обновлен.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Product> readFromExternalFile() {
        List<Product> allProduct = new ArrayList<>();
        String path = "db_files/product.sr";

        File file = new File(path);
        if (!file.isFile() || !file.exists() || file.length() == 0) {
            System.out.println("Файл БД не существует или пуст");
            return allProduct;
        }

        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file))) {
            allProduct = (List<Product>) objectInputStream.readObject();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return allProduct;
    }

    /**
     * Метод инициализирующий процесс загрузки тестовых данных в "кэш" может применяться в тестах
     *
     * @param productDB  кэш БД для загрузки данных по доступным товарам
     * @param categoryDB кэш БД для загрузки данных по доступным категориям товаров
     * @param brandDB    кэш БД для загрузки данных по доступным брэндам
     */
    public static void initTestInMemoryBase(ProductDB productDB,
                                            CategoryDB categoryDB,
                                            BrandDB brandDB) {
        Product prd_1 = Product.builder()
                .name("Веник")
                .price(12.00)
                .category(categoryDB.findById(2).get())
                .brand(brandDB.getById(3).get())
                .description("Очень крутой веник")
                .stockQuantity(12)
                .lastModifiedTimestamp(new Date().getTime())
                .build();

        Product prd_2 = Product.builder()
                .name("Дрель")
                .price(35.00)
                .category(categoryDB.findById(3).get())
                .brand(brandDB.getById(1).get())
                .description("Кручу-верчу")
                .stockQuantity(2)
                .lastModifiedTimestamp(new Date().getTime())
                .build();

        Product prd_3 = Product.builder()
                .name("Валенки")
                .price(135.00)
                .category(categoryDB.findById(1).get())
                .brand(brandDB.getById(2).get())
                .description("Танцы на льду")
                .stockQuantity(145)
                .lastModifiedTimestamp(new Date().getTime())
                .build();

        productDB.add(prd_1);
        productDB.add(prd_2);
        productDB.add(prd_3);
    }
}