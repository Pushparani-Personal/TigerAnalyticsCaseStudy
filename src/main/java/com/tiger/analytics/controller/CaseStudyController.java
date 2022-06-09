package com.tiger.analytics.controller;

import com.tiger.analytics.exception.CaseStudyExceptionHandler;
import com.tiger.analytics.exception.ResponseException;
import com.tiger.analytics.model.StoreProduct;
import com.tiger.analytics.service.CaseStudyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class CaseStudyController {

  @Autowired
  CaseStudyService caseStudyService;

  private final static Logger logger = LoggerFactory.getLogger(CaseStudyController.class);

  /**
   * Endpoint to get product details
   * @return products
   */
  @Operation(summary = "Get Products", responses = {
      @ApiResponse(description = "Products",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = StoreProduct.class)))})
  @GetMapping(value = "/view")
  public ResponseEntity<?> getProducts() {
    HttpHeaders httpHeaders = new HttpHeaders();
    HttpStatus status;
    try {
        List<StoreProduct> storeProducts = caseStudyService.getProductDetails();
        status = HttpStatus.OK;
        return new ResponseEntity<>(storeProducts, httpHeaders, status);
    } catch (CaseStudyExceptionHandler caseStudyExceptionHandler) {
      logger.error("Failed to fetch product details: ", caseStudyExceptionHandler);
      status = caseStudyExceptionHandler.getExceptionCode();
      ResponseException responseException = new ResponseException(new Date(), status.value(), caseStudyExceptionHandler.getExceptionError(), caseStudyExceptionHandler.getExceptionReason());
      return new ResponseEntity<>(responseException, httpHeaders, status);
    } catch (Exception exception) {
      logger.error("Failed to fetch product details: ", exception);
      status = HttpStatus.INTERNAL_SERVER_ERROR;
      ResponseException responseException = new ResponseException(new Date(), status.value(), status.getReasonPhrase(), "Error occurred while trying to fetch product details from CSV");
      return new ResponseEntity<>(responseException, httpHeaders, status);
    }
  }

  /**
   * Endpoint used to create products
   * @param storeProduct
   * @return products
   */
  @Operation(summary = "Create Products", responses = {
      @ApiResponse(description = "Products",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = StoreProduct.class)))})
  @PostMapping(value = "/create")
  public ResponseEntity<?> createProduct(
      @RequestBody StoreProduct storeProduct
  ) {
    HttpHeaders httpHeaders = new HttpHeaders();
    HttpStatus status;
    Map<String, String> responseMessage = new HashMap<>();
    try {
      caseStudyService.createProduct(storeProduct);
      status = HttpStatus.OK;
      responseMessage.put("message", "Successfully Created product for SKU: " + storeProduct.getSku());
      responseMessage.put("status", String.valueOf(status.value()));
      return new ResponseEntity<>(responseMessage, httpHeaders, status);
    } catch (CaseStudyExceptionHandler caseStudyExceptionHandler) {
      logger.error("Failed to update product details: ", caseStudyExceptionHandler);
      status = caseStudyExceptionHandler.getExceptionCode();
      ResponseException responseException = new ResponseException(new Date(), status.value(), caseStudyExceptionHandler.getExceptionError(), caseStudyExceptionHandler.getExceptionReason());
      return new ResponseEntity<>(responseException, httpHeaders, status);
    } catch (Exception exception) {
      logger.error("Failed to update product details: ", exception);
      status = HttpStatus.INTERNAL_SERVER_ERROR;
      ResponseException responseException = new ResponseException(new Date(), status.value(), status.getReasonPhrase(), "Error occurred while trying to update product details to CSV");
      return new ResponseEntity<>(responseException, httpHeaders, status);
    }
  }

  /**
   * Endpoint used to update products
   * @param storeProduct
   * @return products
   */
  @Operation(summary = "Save Products", responses = {
      @ApiResponse(description = "Products",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = StoreProduct.class)))})
  @PutMapping(value = "/update")
  public ResponseEntity<?> updateProducts(
      @RequestBody StoreProduct storeProduct
  ) {
    HttpHeaders httpHeaders = new HttpHeaders();
    HttpStatus status;
    Map<String, String> responseMessage = new HashMap<>();
    try {
      caseStudyService.updateProduct(storeProduct);
      status = HttpStatus.OK;
      responseMessage.put("message", "Successfully Updated product for SKU: " + storeProduct.getSku());
      responseMessage.put("status", String.valueOf(status.value()));
      return new ResponseEntity<>(responseMessage, httpHeaders, status);
    }  catch (CaseStudyExceptionHandler caseStudyExceptionHandler) {
      logger.error("Failed to update product details: ", caseStudyExceptionHandler);
      status = caseStudyExceptionHandler.getExceptionCode();
      ResponseException responseException = new ResponseException(new Date(), status.value(), caseStudyExceptionHandler.getExceptionError(), caseStudyExceptionHandler.getExceptionReason());
      return new ResponseEntity<>(responseException, httpHeaders, status);
    } catch (Exception exception) {
      logger.error("Failed to update product details: ", exception);
      status = HttpStatus.INTERNAL_SERVER_ERROR;
      ResponseException responseException = new ResponseException(new Date(), status.value(), status.getReasonPhrase(), "Error occurred while trying to update product details to CSV");
      return new ResponseEntity<>(responseException, httpHeaders, status);
    }
  }
}
