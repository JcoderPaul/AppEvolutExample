package me.oldboy.market.controlers;

import lombok.AllArgsConstructor;
import me.oldboy.market.entity.Product;
import me.oldboy.market.entity.log_enum.Action;
import me.oldboy.market.entity.log_enum.Status;
import me.oldboy.market.exceptions.ProductCrudControllerException;
import me.oldboy.market.exceptions.ProductServiceException;
import me.oldboy.market.services.AuditService;
import me.oldboy.market.services.ProductService;

/**
 * Класс контролирует основные операции с товаром Product и записывает все процедуры в аудит-лог БД
 */
@AllArgsConstructor
public class ProductCrudController {
    private ProductService productService;
    private AuditService auditService;

    /**
     * Метод готовит запись о новом товаре Product в БД (без ID), и аудирует процесс создания.
     *
     * @param product создаваемый товар (продукт)
     * @param email   электронный адрес пользователя создавшего запись
     * @return созданный товар содержащий уникальный ID товара в БД
     */
    public Product createProduct(Product product, String email) {
        if (productService.findProductByBrandAndName(product.getBrand(), product.getName()) != null) {
            throw new ProductCrudControllerException("Duplicate product");
        }
        Product createdProduct = productService.createProduct(product);
        if (createdProduct.getId() != null) {
            auditService.saveAuditRecord(Action.ADD_PRODUCT, Status.SUCCESS, email, createdProduct);
        } else {
            auditService.saveAuditRecord(Action.ADD_PRODUCT, Status.FAIL, email, product);
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
        try {
            Product foundProduct = productService.findProductById(product.getId());

            if (foundProduct.getCategory().equals(product.getCategory()) &&
                    foundProduct.getBrand().equals(product.getBrand())) {

                productService.updateProduct(product);
                auditService.saveAuditRecord(Action.UPDATE_PRODUCT, Status.SUCCESS, email, product);
            } else {
                throw new ProductCrudControllerException("Unable to update category or brand, please create a new product.");
            }
        } catch (ProductServiceException | ProductCrudControllerException e) {
            auditService.saveAuditRecord(Action.UPDATE_PRODUCT, Status.FAIL, email, product);
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
        try {
            Product toAuditRecord = cloneProduct(productService.findProductById(productId));
            isDelete = productService.deleteProduct(productId);
            if (isDelete) {
                auditService.saveAuditRecord(Action.DELETE_PRODUCT, Status.SUCCESS, email, toAuditRecord);
            } else {
                auditService.saveAuditRecord(Action.UPDATE_PRODUCT, Status.FAIL, email, toAuditRecord);
            }
        } catch (ProductServiceException e) {
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
            return productService.findProductById(productId);
        } catch (ProductServiceException e) {
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
                .category(product.getCategory())
                .brand(product.getBrand())
                .description(product.getDescription())
                .stockQuantity(product.getStockQuantity())
                .creationTimestamp(product.getCreationTimestamp())
                .lastModifiedTimestamp(product.getLastModifiedTimestamp())
                .build();

        return cloneProd;
    }
}
