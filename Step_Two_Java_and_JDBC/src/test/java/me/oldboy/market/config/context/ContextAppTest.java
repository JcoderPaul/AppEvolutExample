package me.oldboy.market.config.context;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ContextAppTest {

    @Mock
    private Connection mockConnection;

    ContextApp contextApp = new ContextApp(mockConnection);

    @Test
    void getAuditRepository_Test() {
        assertThat(contextApp.getAuditRepository()).isNotNull();
    }

    @Test
    void getBrandRepository_Test() {
        assertThat(contextApp.getBrandRepository()).isNotNull();
    }

    @Test
    void getCategoryRepository_Test() {
        assertThat(contextApp.getCategoryRepository()).isNotNull();
    }

    @Test
    void getProductRepository_Test() {
        assertThat(contextApp.getProductRepository()).isNotNull();
    }

    @Test
    void getUserRepository_Test() {
        assertThat(contextApp.getUserRepository()).isNotNull();
    }

    @Test
    void getAuditService_Test() {
        assertThat(contextApp.getAuditService()).isNotNull();
    }

    @Test
    void getProductService_Test() {
        assertThat(contextApp.getProductService()).isNotNull();
    }

    @Test
    void getUserService_Test() {
        assertThat(contextApp.getUserService()).isNotNull();
    }

    @Test
    void getBrandService_Test() {
        assertThat(contextApp.getBrandService()).isNotNull();
    }

    @Test
    void getCategoryService_Test() {
        assertThat(contextApp.getCategoryService()).isNotNull();
    }

    @Test
    void getLoginLogoutController_Test() {
        assertThat(contextApp.getLoginLogoutController()).isNotNull();
    }

    @Test
    void getProductCrudController_Test() {
        assertThat(contextApp.getProductCrudController()).isNotNull();
    }

    @Test
    void getViewProductController_Test() {
        assertThat(contextApp.getViewProductController()).isNotNull();
    }

    @Test
    void getViewBrandController_Test() {
        assertThat(contextApp.getViewBrandController()).isNotNull();
    }

    @Test
    void getViewCategoryController_Test() {
        assertThat(contextApp.getViewCategoryController()).isNotNull();
    }

    @Test
    void getViewAuditRecordController_Test() {
        assertThat(contextApp.getViewAuditRecordController()).isNotNull();
    }

    @Test
    void getInputExistChecker_Test() {
        assertThat(contextApp.getInputExistChecker()).isNotNull();
    }
}