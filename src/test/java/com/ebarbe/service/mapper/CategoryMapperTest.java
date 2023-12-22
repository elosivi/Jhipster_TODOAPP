package com.ebarbe.service.mapper;

import org.junit.jupiter.api.BeforeEach;

class CategoryMapperTest {

    private CategoryMapper categoryMapper;

    @BeforeEach
    public void setUp() {
        categoryMapper = new CategoryMapperImpl();
    }
}
