package me.oldboy.market.controlers;

import lombok.AllArgsConstructor;
import me.oldboy.market.entity.Audit;
import me.oldboy.market.entity.Product;
import me.oldboy.market.entity.enums.Action;
import me.oldboy.market.entity.enums.Status;
import me.oldboy.market.exceptions.ProductCrudControllerException;
import me.oldboy.market.exceptions.ServiceLayerException;
import me.oldboy.market.services.AuditServiceImpl;
import me.oldboy.market.services.ProductServiceImpl;

import java.time.LocalDateTime;

/**
 * Класс контролирует основные операции с товаром Product и записывает все процедуры в аудит-лог БД
 */
@AllArgsConstructor
public class ProductCrudController {
    private ProductServiceImpl productService;
    private AuditServiceImpl auditService;

    /**
     * Метод готовит запись о новом товаре Product в БД (без ID), и аудирует процесс создания.
     *
     * @param product создаваемый товар (продукт)
     * @param email   электронный адрес пользователя создавшего запись
     * @return созданный товар содержащий уникальный ID товара в БД
     */
    public Product createProduct(Product product, String email) {

        Audit auditRecord = Audit.builder()
                .createAt(LocalDateTime.now())
                .createBy(email)
                .action(Action.ADD_PRODUCT)
                .build();

        if (productService.findProductByBrandAndName(product.getBrandId(), product.getName()) != null) {
            throw new ProductCrudControllerException("Duplicate product name");
        }
        Product createdProduct = productService.create(product);
        if (createdProduct.getId() != null) {
            auditRecord.setIsSuccess(Status.SUCCESS);
            auditRecord.setAuditableRecord(createdProduct.toString());

            auditService.create(auditRecord);
        } else {
            auditRecord.setIsSuccess(Status.FAIL);
            auditRecord.setAuditableRecord(createdProduct.toString());

            auditService.create(auditRecord);
        }
        return createdProduct;
    }

    /**
     * Метод готовит обновление существующей в БД записи о продукте (товаре, Product) и фиксирует пользователя сделавшего изменения
     *
     * @param product товар который нужно обновить (содержит уникальный ID, по-которому его идентифицируют)
     * @param email   электронный адрес пользователя изменившего сведения о товаре
     */
    public void updateProduct(Product product, String email) {

        Audit auditUpdate = Audit.builder()
                .createAt(LocalDateTime.now())
                .createBy(email)
                .action(Action.UPDATE_PRODUCT)
                .build();

        try {
            Product foundProduct = productService.findById(product.getId());

            if (foundProduct.getCategoryId().equals(product.getCategoryId()) &&
                    foundProduct.getBrandId().equals(product.getBrandId())) {

                productService.update(product);

                auditUpdate.setIsSuccess(Status.SUCCESS);
                auditUpdate.setAuditableRecord(product.toString());

                auditService.create(auditUpdate);
            } else {
                throw new ProductCrudControllerException("Unable to update category or brand, please create a new product.");
            }
        } catch (ServiceLayerException | ProductCrudControllerException e) {
            auditUpdate.setIsSuccess(Status.FAIL);
            auditUpdate.setAuditableRecord(product.toString());

            auditService.create(auditUpdate);
            System.out.println(e.getMessage());
        }
    }

    /**
     * Метод готовит удаление товара Product из БД и фиксирующий пользователя проведшего операцию.
     *
     * @param productId уникальный идентификационный номер ID удаляемого товара
     * @param email     электронный адрес пользователя удаляющего товар Product
     * @return true - если товар удален, в противном случае - false
     */
    public boolean deleteProduct(Long productId, String email) {
        boolean isDelete = false;

        Audit auditRecord = Audit.builder()
                .createAt(LocalDateTime.now())
                .createBy(email)
                .action(Action.DELETE_PRODUCT)
                .build();

        try {
            Product toAuditRecord = cloneProduct(productService.findById(productId));
            isDelete = productService.delete(productId);
            if (isDelete) {
                auditRecord.setIsSuccess(Status.SUCCESS);
                auditRecord.setAuditableRecord(toAuditRecord.toString());

                auditService.create(auditRecord);
            } else {
                auditRecord.setIsSuccess(Status.FAIL);
                auditRecord.setAuditableRecord(toAuditRecord.toString());

                auditService.create(auditRecord);
            }
        } catch (ServiceLayerException e) {
            System.out.println(e.getMessage());
        }
        return isDelete;
    }

    /**
     * Ищет товар по уникальному идентификационному номеру ID в БД
     *
     * @param productId идентификатор товара в БД
     * @return найденный товар в случае успеха и null - если товар не найден
     */
    public Product findProductById(Long productId) {
        try {
            return productService.findById(productId);
        } catch (ServiceLayerException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * Клонирует товар для целей аудита в процессе удаления
     *
     * @param product оригинальный товар для клонирования перед удалением
     * @return полный клон товара сохраняемый в "кэш таблицы" аудита.
     */
    private Product cloneProduct(Product product) {
        Product cloneProd = Product.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .categoryId(product.getCategoryId())
                .brandId(product.getBrandId())
                .description(product.getDescription())
                .stockQuantity(product.getStockQuantity())
                .creationAt(product.getCreationAt())
                .modifiedAt(product.getModifiedAt())
                .build();

        return cloneProd;
    }
}