package com.seng302.wasteless.unitTest.ServiceTests;


import com.seng302.wasteless.repository.ProductRepository;
import com.seng302.wasteless.service.ProductService;
import com.seng302.wasteless.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import com.seng302.wasteless.model.*;


@RunWith(SpringRunner.class)
@WebMvcTest(ProductService.class)
class ProductServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private UserService userService;


    @Test
    void whenCheckProductBelongsToBusiness_AndNoMatch_ThrowException() {
        boolean success = true;
        try {
            ProductService productService = new ProductService(productRepository);
            Product product = new Product();
            product.setBusinessId(1);
            productService.checkProductBelongsToBusiness(product, 2);
        } catch (ResponseStatusException e) {
            success = false;
            assertEquals(400, e.getRawStatusCode());
        }
        assert !success;


    }
    @Test
    void whenFindProductById_AndInvalidId_ThrowException() {
        Mockito.when(productRepository.findFirstById("test")).thenReturn(null);
        boolean success = true;
        try {
            ProductService productService = new ProductService(productRepository);
            productService.findProductById("test");
        } catch (ResponseStatusException e) {
            success = false;
            assertEquals(400, e.getRawStatusCode());
        }
        assert !success;


    }




}

