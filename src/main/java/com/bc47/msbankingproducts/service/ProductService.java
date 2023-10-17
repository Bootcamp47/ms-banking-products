package com.bc47.msbankingproducts.service;

import com.bc47.msbankingproducts.api.ProductsApiDelegate;
import com.bc47.msbankingproducts.entity.Product;
import com.bc47.msbankingproducts.model.ProductDTO;
import com.bc47.msbankingproducts.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService implements ProductsApiDelegate {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public ResponseEntity<List<ProductDTO>> retrieveAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductDTO> productDTOList =
                products
                        .stream()
                        .map(this::createDTO)
                        .collect(Collectors.toList());
        return new ResponseEntity<>(productDTOList, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ProductDTO> retrieveProduct(String id) {
        List<Product> products = productRepository.findAll();
        Optional<ProductDTO> productFound =
                products
                        .stream()
                        .filter(p -> Objects.equals(p.getId(), id))
                        .map(this::createDTO)
                        .findFirst();
        return productFound.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.ok(new ProductDTO()));
    }

    @Override
    public ResponseEntity<ProductDTO> saveProduct(ProductDTO productDTO) {
        Product product = Product
                .builder()
                .id(productDTO.getId())
                .productType(productDTO.getProductType())
                .productCategory(productDTO.getProductCategory())
                .state(productDTO.getState())
                .createdAt(new Date().toString())
                .build();
        productRepository.save(product);
        productDTO.setId(product.getId());
        productDTO.setCreatedAt(new Date().toString());
        return ResponseEntity.ok(productDTO);
    }

    @Override
    public ResponseEntity<ProductDTO> updateProduct(ProductDTO productDTO) {
        return saveProduct(productDTO);
    }

    @Override
    public ResponseEntity<ProductDTO> deleteProduct(String id) {
        List<Product> products = productRepository.findAll();
        Optional<ProductDTO> productFound =
                products
                        .stream()
                        .filter(p -> Objects.equals(p.getId(), id))
                        .map(product -> {
                            productRepository.deleteById(product.getId());
                            return createDTO(product);
                        })
                        .findFirst();
        return productFound.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.ok(new ProductDTO()));
    }

    private ProductDTO createDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setProductType(product.getProductType());
        productDTO.setProductCategory(product.getProductCategory());
        productDTO.setState(product.getState());
        productDTO.setCreatedAt(product.getCreatedAt());
        return productDTO;
    }
}
