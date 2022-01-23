package org.contab2;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

class ArchTest {

    @Test
    void servicesAndRepositoriesShouldNotDependOnWebLayer() {
        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("org.contab2");

        noClasses()
            .that()
            .resideInAnyPackage("org.contab2.service..")
            .or()
            .resideInAnyPackage("org.contab2.repository..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("..org.contab2.web..")
            .because("Services and repositories should not depend on web layer")
            .check(importedClasses);
    }
}
