package com.tiger.analytics.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import com.opencsv.exceptions.CsvException;
import com.tiger.analytics.exception.CaseStudyExceptionHandler;
import com.tiger.analytics.model.StoreProduct;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
public class CaseStudyService {

  private final static Logger logger = LoggerFactory.getLogger(CaseStudyService.class);

  /**
   * Method used to fetch data from CSV
   * @return List<StoreProduct>
   * @throws CaseStudyExceptionHandler
   */
  public List<StoreProduct> getProductDetails() throws CaseStudyExceptionHandler {
    try (CSVReader csvReader = new CSVReader(new FileReader("ProductList.csv"))) {
      CsvToBean product = new CsvToBean();
      product.setMappingStrategy(setColumnMapping());
      product.setCsvReader(csvReader);
      product.setIgnoreEmptyLines(true);
      product.setOrderedResults(true);

      List<StoreProduct> storeProducts = product.parse();
      return (!CollectionUtils.isEmpty(storeProducts) ? storeProducts : new ArrayList<>());
    } catch (FileNotFoundException fileNotFoundException) {
      logger.error("Unable to find source file." + fileNotFoundException);
      throw new CaseStudyExceptionHandler(HttpStatus.NOT_FOUND, "Source not found", "Failed to fetch source file from given path.");
    } catch (CaseStudyExceptionHandler caseStudyExceptionHandler) {
      logger.error("Unable to retrieve data from csv." + caseStudyExceptionHandler);
      throw new CaseStudyExceptionHandler(caseStudyExceptionHandler.getExceptionCode(), caseStudyExceptionHandler.getExceptionError(), caseStudyExceptionHandler.getExceptionReason());
    } catch (Exception exception) {
      logger.error("Unable to retrieve data from csv." + exception);
      throw new CaseStudyExceptionHandler(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get data", "Unable to retrieve data from csv.");
    }
  }

  /**
   * Method used to map header value to store product
   * @return HeaderStrategy
   * @throws CaseStudyExceptionHandler
   */
  private static HeaderColumnNameTranslateMappingStrategy setColumnMapping() throws CaseStudyExceptionHandler {
    try {
      HeaderColumnNameTranslateMappingStrategy<StoreProduct> strategy = new HeaderColumnNameTranslateMappingStrategy<>();
      strategy.setType(StoreProduct.class);
      Map<String, String> mapRows = new HashMap<>();
      Arrays.stream(StoreProduct.class.getDeclaredFields()).forEach(field -> {
        mapRows.put(field.getName(), field.getName());
      });
      strategy.setColumnMapping(mapRows);
      return strategy;
    } catch (Exception exception) {
      logger.error("Fail to map header value." + exception);
      throw new CaseStudyExceptionHandler(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to map header", "Unable to map header to store product");
    }
  }

  /**
   * Method used to update records in csv
   * @param storeProduct
   * @throws CaseStudyExceptionHandler
   */
  public void updateProduct(StoreProduct storeProduct) throws CaseStudyExceptionHandler {

    try {
      boolean isUpdated = false;
      CSVReader csvReader = new CSVReader(new FileReader("ProductList.csv"));
      String[] header = csvReader.readNext();
      int skuIndex = Arrays.asList(header).indexOf("sku");
      List<String[]> allProducts = csvReader.readAll();
      Iterator<String[]> products = allProducts.iterator();
      while (!isUpdated && products.hasNext()) {
        String[] product = products.next();
        if (product[skuIndex].equals(storeProduct.getSku())) {
          int productIndex = Integer.parseInt(product[0]) - 1;
          String[] updateRecord = newRecord(storeProduct, header, product[0]);
          allProducts.set(productIndex, updateRecord);
          isUpdated = true;
        }
      }

      csvReader.close();
      // Update modified records to CSV
      CSVWriter csvWriter = new CSVWriter(new FileWriter("ProductList.csv"));
      csvWriter.writeAll(Collections.singleton(header));
      csvWriter.writeAll(allProducts);
      csvWriter.close();
    } catch (FileNotFoundException fileNotFoundException) {
      logger.error("Unable to find source file." + fileNotFoundException);
      throw new CaseStudyExceptionHandler(HttpStatus.NOT_FOUND, "Source not found", "Failed to fetch source file from given path.");
    } catch (CsvException csvException) {
      logger.error("Trying to update invalid product details" + csvException);
      throw new CaseStudyExceptionHandler(HttpStatus.BAD_REQUEST, "Invalid Data", "Failed to update invalid product details.");
    } catch (CaseStudyExceptionHandler caseStudyExceptionHandler) {
      logger.error("Trying to update invalid product details." + caseStudyExceptionHandler);
      throw new CaseStudyExceptionHandler(caseStudyExceptionHandler.getExceptionCode(), caseStudyExceptionHandler.getExceptionError(), caseStudyExceptionHandler.getExceptionReason());
    } catch (Exception exception) {
      logger.error("Trying to update invalid product details." + exception);
      throw new CaseStudyExceptionHandler(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update data", "Unable to update data to csv.");
    }
  }

  /**
   * Method used to update records in csv
   * @param storeProduct
   * @throws CaseStudyExceptionHandler
   */
  public void createProduct(StoreProduct storeProduct) throws CaseStudyExceptionHandler {

    try {
      CSVReader csvReader = new CSVReader(new FileReader("ProductList.csv"));
      String[] header = csvReader.readNext();
      List<String[]> products = csvReader.readAll();
      int nextIndex = products.size() + 1;
      if (!StringUtils.hasText(storeProduct.getSku()) || products.stream().anyMatch(product -> Arrays.stream(product).anyMatch(item -> item.equals(storeProduct.getSku())))) {
        throw new CaseStudyExceptionHandler(HttpStatus.BAD_REQUEST, "Duplicate Product", "Product with sku id: " + storeProduct.getSku() + " already exists. Please add product with different SKU");
      }
      csvReader.close();
      // Add new product to CSV
      String[] newProduct = newRecord(storeProduct, header, String.valueOf(nextIndex));
      products.add(newProduct);
      CSVWriter csvWriter = new CSVWriter(new FileWriter("ProductList.csv"));
      csvWriter.writeNext(header);
      csvWriter.writeAll(products);
      csvWriter.close();
    } catch (FileNotFoundException fileNotFoundException) {
      logger.error("Unable to fetch source file." + fileNotFoundException);
      throw new CaseStudyExceptionHandler(HttpStatus.NOT_FOUND, "Source not found", "Failed to fetch source file from given path.");
    } catch (CsvException csvException) {
      logger.error("Trying to add invalid product details" + csvException);
      throw new CaseStudyExceptionHandler(HttpStatus.BAD_REQUEST, "Invalid Data", "Failed to create invalid product details.");
    } catch (CaseStudyExceptionHandler caseStudyExceptionHandler) {
      logger.error("Trying to add invalid product details." + caseStudyExceptionHandler);
      throw new CaseStudyExceptionHandler(caseStudyExceptionHandler.getExceptionCode(), caseStudyExceptionHandler.getExceptionError(), caseStudyExceptionHandler.getExceptionReason());
    } catch (Exception exception) {
      logger.error("Trying to add invalid product details." + exception);
      throw new CaseStudyExceptionHandler(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create data", "Unable to add new product to csv.");
    }
  }

  /**
   * Map updated product with csv header column
   * @param storeProduct
   * @param header
   * @param index
   * @return product[]
   * @throws CaseStudyExceptionHandler
   */
  private String[] newRecord(StoreProduct storeProduct, String[] header, String index) throws CaseStudyExceptionHandler {
    try {
      String[] newRecord = new String[header.length];
      newRecord[Arrays.asList(header).indexOf("productIndex")] = index;
      newRecord[Arrays.asList(header).indexOf("storeId")] = storeProduct.getStoreId();
      newRecord[Arrays.asList(header).indexOf("sku")] = storeProduct.getSku();
      newRecord[Arrays.asList(header).indexOf("productName")] = storeProduct.getProductName();
      newRecord[Arrays.asList(header).indexOf("price")] = String.valueOf(storeProduct.getPrice());
      newRecord[Arrays.asList(header).indexOf("date")] = storeProduct.getDate();
      newRecord[Arrays.asList(header).indexOf("description")] = storeProduct.getDescription();
      newRecord[Arrays.asList(header).indexOf("imageUrl")] = storeProduct.getImageUrl();
      return newRecord;
    } catch (Exception exception) {
      logger.error("Fail to updated product with csv header column ." + exception);
      throw new CaseStudyExceptionHandler(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to map data with header", "Failed to map updated product with csv header column");
    }
  }
}
