package me.oldboy.market.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import me.oldboy.market.productmanager.core.dto.brand.BrandReadDto;
import me.oldboy.market.productmanager.core.services.interfaces.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST контроллер для управления брендами.
 * Предоставляет API для работы с брендами товаров.
 * Все методы возвращают данные в формате JSON.
 */
@Slf4j
@RestController
@RequestMapping("/market/brands")
@Tag(name = "BrandController", description = "Реализует просмотровые методы для брэндов товара (продукта)")
public class BrandController {

    private final BrandService brandService;

    @Autowired
    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    /**
     * Получает бренд по его идентификатору.
     * Если бренд с указанным идентификатором существует, возвращает его данные.
     * В противном случае возвращает HTTP 404 Not Found.
     *
     * @param brandId идентификатор бренда
     * @return {@link ResponseEntity} с найденным брендом или статусом 404
     * @apiExample Пример запроса: GET /market/brands/1
     */
    @GetMapping("/{brandId}")
    @Operation(summary = "Поиск брэнда по его уникальному идентификатору",
            description = "Возвращает информацию о брэнде по названию или статус контент не найден")
    public ResponseEntity<?> getBrandById(@PathVariable("brandId")
                                          Integer brandId) {
        return brandService.findById(brandId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Получает все бренды из БД.
     * Возвращает полный список всех доступных брендов в формате JSON.
     *
     * @return {@link ResponseEntity} со списком всех брендов
     * @apiExample Пример запроса: GET /market/brands
     */
    @GetMapping()
    @Operation(summary = "Поиск всех доступных брэндов",
            description = "Возвращает информацию о всех доступных брэндах")
    public ResponseEntity<?> getAllBrands() {
        List<BrandReadDto> brandList = brandService.findAll();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(brandList);
    }

    /**
     * Находит бренд по названию.
     * Если бренд с указанным названием существует, возвращает его данные.
     * В противном случае возвращает HTTP 404 Not Found.
     *
     * @param brandName название бренда для поиска
     * @return {@link ResponseEntity} с найденным брендом или статусом 404
     * @apiExample Пример запроса: GET /market/brands/?brandName=MeCOOL
     */
    @GetMapping("/")
    @Operation(summary = "Поиск брэнда по его названию",
            description = "Возвращает информацию о найденном брэнде или статус контент не найден")
    public ResponseEntity<?> getBrandByName(@RequestParam(name = "brandName", required = false)
                                            String brandName) {
        return brandService.findByName(brandName)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}