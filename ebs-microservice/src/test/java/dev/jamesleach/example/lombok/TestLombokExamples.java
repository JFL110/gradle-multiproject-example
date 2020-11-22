package dev.jamesleach.example.lombok;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestLombokExamples {

    @Test
    public void testBuilder() {
        LombokBuilderExample built = new LombokBuilderExample.LombokBuilderExampleBuilder()
                .name("the-name")
                .value("the-value")
                .build();

        assertEquals("the-name", built.getName());
        assertEquals("the-value", built.getValue());
    }

    @Test(expected = NullPointerException.class)
    public void testBuilderUnspecified() {
        new LombokBuilderExample.LombokBuilderExampleBuilder()
                .name("the-name")
                .build();
    }
}
