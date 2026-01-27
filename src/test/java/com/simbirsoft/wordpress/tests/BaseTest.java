package com.simbirsoft.wordpress.tests;

import com.simbirsoft.wordpress.helpers.DbHelper;
import com.simbirsoft.wordpress.services.PostsService;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.asserts.SoftAssert;

public abstract class BaseTest {
    protected PostsService postsService;
    protected DbHelper dbHelper;
    protected SoftAssert softAssert;

    @BeforeClass
    public void setUp() {
        postsService = new PostsService();
        dbHelper = new DbHelper();
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        dbHelper.closeConnection();
    }
}