package me.oldboy.market.config_context;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ContextAppTest {

    private static ContextApp contextApp;

    @BeforeEach
    void setUp(){
        contextApp = new ContextApp();
    }

    @AfterEach
    void eraseBase(){
        contextApp.getAuditDB().getAuditLogList().clear();

        contextApp.getCategoryDB().getIndexCategory().clear();
        contextApp.getCategoryDB().getCategoryList().clear();

        contextApp.getBrandDB().getIndexBrand().clear();
        contextApp.getBrandDB().getBrandList().clear();

        contextApp.getProductDB().getCategoryIndex().clear();
        contextApp.getProductDB().getProductsList().clear();
        contextApp.getProductDB().getProductByIdIndex().clear();
        contextApp.getProductDB().getBrandIndex().clear();

        contextApp.getUserDB().getUserDb().clear();
    }

    @Test
    void getAuditDB() {
        assertThat(contextApp.getAuditDB()).isNotNull();
    }

    @Test
    void getUserDB() {
        assertThat(contextApp.getUserDB()).isNotNull();
    }

    @Test
    void getCategoryDB() {
        assertThat(contextApp.getCategoryDB()).isNotNull();
    }

    @Test
    void getProductDB() {
        assertThat(contextApp.getProductDB()).isNotNull();
    }

    @Test
    void getBrandDB() {
        assertThat(contextApp.getBrandDB()).isNotNull();
    }

    @Test
    void getAuditRepository() {
        assertThat(contextApp.getAuditRepository()).isNotNull();
    }

    @Test
    void getBrandRepository() {
        assertThat(contextApp.getBrandRepository()).isNotNull();
    }

    @Test
    void getCategoryRepository() {
        assertThat(contextApp.getCategoryRepository()).isNotNull();
    }

    @Test
    void getProductRepository() {
        assertThat(contextApp.getProductRepository()).isNotNull();
    }

    @Test
    void getUserRepository() {
        assertThat(contextApp.getUserRepository()).isNotNull();
    }

    @Test
    void getAuditService() {
        assertThat(contextApp.getAuditService()).isNotNull();
    }

    @Test
    void getProductService() {
        assertThat(contextApp.getProductService()).isNotNull();
    }

    @Test
    void getUserService() {
        assertThat(contextApp.getUserService()).isNotNull();
    }

    @Test
    void getLoginLogoutController() {
        assertThat(contextApp.getLoginLogoutController()).isNotNull();
    }

    @Test
    void getProductCrudController() {
        assertThat(contextApp.getProductCrudController()).isNotNull();
    }

    @Test
    void getViewProductController() {
        assertThat(contextApp.getViewProductController()).isNotNull();
    }

    @Test
    void getViewBrandController() {
        assertThat(contextApp.getViewBrandController()).isNotNull();
    }

    @Test
    void getViewCategoryController() {
        assertThat(contextApp.getViewCategoryController()).isNotNull();
    }

    @Test
    void getViewAuditRecordController() {
        assertThat(contextApp.getViewAuditRecordController()).isNotNull();
    }
}