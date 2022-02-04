package com.marcura.currency.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RatesScheduler.class}, loader = AnnotationConfigContextLoader.class)
class RatesSchedulerTest {

    @Test
    public void testScheduledAnnotation() throws InterruptedException {
        Thread.sleep(5000);
    }
}