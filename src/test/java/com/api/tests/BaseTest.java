package com.api.tests;

import com.api.domain.service.CommentsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;

public abstract class BaseTest {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected CommentsService commentsService;

    @BeforeClass
    public void setUp() {
        commentsService = new CommentsService();
        log.info("=== Test class initialized: {} ===", getClass().getSimpleName());
    }
}

