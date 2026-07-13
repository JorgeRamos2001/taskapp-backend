package com.taskapp;

import com.enofex.taikai.Taikai;
import org.junit.jupiter.api.Test;

class ArchUnitTests {

    private static final String BASE_PACKAGE = "com.taskapp";

    @Test
    void shouldFulfillConstraints() {
        Taikai.builder()
                .namespace(BASE_PACKAGE)
                .java(java -> java
                        .noUsageOfDeprecatedAPIs())
                .spring(spring -> spring
                        .noAutowiredFields()
                        .controllers(c -> c
                                .shouldBeAnnotatedWithRestController()
                                .namesShouldEndWithController()
                                .shouldNotDependOnOtherControllers()
                                .shouldBePackagePrivate())
                        .services(s -> s
                                .shouldNotDependOnControllers())
                        .repositories(r -> r
                                .shouldNotDependOnServices()
                                .namesShouldEndWithRepository()))
                .build()
                .checkAll();
    }
}
