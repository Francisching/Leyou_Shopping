package com.leyou.item.service;

import com.leyou.item.dto.BrandDTO;
import com.leyou.item.dto.CategoryDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BrandServiceTest {

    @Autowired
    private BrandService brandService;

    @Test
    public void testBrandAdd(){
        BrandDTO brandDTO = new BrandDTO();
        brandDTO.setLetter('H');
        brandDTO.setName("黑马");

        List<Long> cids = Arrays.asList(76L,266L,683L);

        this.brandService.addBrand(brandDTO,cids);
    }

}