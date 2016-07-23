package com.birdben.utils.crawler.app;

import junit.framework.TestCase;
import org.junit.Test;

public class WanshenmeAppTest extends TestCase {

    @Test
    public void testWanshenme() {
        try {
            WanshenmeApp.startCrawler();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}