package com.hieu.corebank.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.EvaluationResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

public class ArchitectureFitnessArchUnitTest {

    private static final Logger log = LoggerFactory.getLogger(ArchitectureFitnessArchUnitTest.class);
    private static JavaClasses importedClasses;

    @BeforeAll
    static void setup() {
        importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.hieu.corebank");
    }

    private void evaluateWithWarning(ArchRule rule, String phaseTarget) {
        EvaluationResult result = rule.evaluate(importedClasses);
        if (result.hasViolation()) {
            log.warn("================================================================================");
            log.warn("[ArchUnit WARN - Planned for {}] Rule violated: {}", phaseTarget, rule.getDescription());
            for (String detail : result.getFailureReport().getDetails()) {
                log.warn("  --> {}", detail);
            }
            log.warn("================================================================================");
        } else {
            log.info("[ArchUnit PASS] {}", rule.getDescription());
        }
    }

    @Test
    @DisplayName("ArchUnit Rule 1: 100% Domain các BC thuần khiết không phụ thuộc Spring / JPA / Jackson (Strict FAIL check)")
    void rule1_domainMustBePure() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .and().haveSimpleNameNotEndingWith("Record") // DomainEventRecord is in eventsourcing
                .should().dependOnClassesThat()
                .resideInAnyPackage("org.springframework..", "jakarta.persistence..", "tools.jackson..", "com.fasterxml.jackson..")
                .as("Domain classes should not depend on Spring, JPA, or Jackson frameworks");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("ArchUnit Rule 1a: account.domain thuần khiết 100%, cấm phụ thuộc Spring, JPA, Jackson (Strict FAIL check - Phase 3)")
    void rule1a_accountDomainMustBePureStrict() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..account.domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("org.springframework..", "jakarta.persistence..", "tools.jackson..", "com.fasterxml.jackson..")
                .as("Account domain must be pure and not depend on Spring, JPA, or Jackson frameworks");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("ArchUnit Rule 1b: shared.domain thuần khiết 100%, cấm phụ thuộc Spring, JPA, Jackson (Strict FAIL check - Phase 3)")
    void rule1b_sharedDomainMustBePureStrict() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..shared.domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("org.springframework..", "jakarta.persistence..", "tools.jackson..", "com.fasterxml.jackson..")
                .as("Shared domain must be pure and not depend on Spring, JPA, or Jackson frameworks");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("ArchUnit Rule 1c: transfer.domain thuần khiết 100%, cấm phụ thuộc Spring, JPA, Jackson (Strict FAIL check - Phase 4)")
    void rule1c_transferDomainMustBePureStrict() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..transfer.domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("org.springframework..", "jakarta.persistence..", "tools.jackson..", "com.fasterxml.jackson..")
                .as("Transfer domain must be pure and not depend on Spring, JPA, or Jackson frameworks");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("ArchUnit Rule 3b: Transfer context không import trực tiếp domain hoặc infrastructure của Account (Strict FAIL check - Phase 4)")
    void rule3b_transferDoesNotDependOnAccountDomainOrInfrastructureStrict() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..transfer..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..account.domain..", "..account.infrastructure..")
                .as("Transfer context should not depend directly on Account domain or infrastructure");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("ArchUnit Rule 2: Application không phụ thuộc vào Infrastructure hoặc API (Warn mode for Phase 2)")
    void rule2_applicationShouldNotDependOnInfrastructureOrApi() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..application..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..infrastructure..", "..api..")
                .as("Application layer should not depend on Infrastructure or API layer");

        evaluateWithWarning(rule, "Phase 4 (Gateways & Ports)");
    }

    @Test
    @DisplayName("ArchUnit Rule 3: Account context không import trực tiếp infrastructure của Customer hoặc Card (Strict FAIL check - Phase 3)")
    void rule3_noDirectCrossBcInfrastructureCoupling() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..account..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..customer.infrastructure..", "..card.infrastructure..")
                .as("Account context should not depend directly on Customer or Card infrastructure");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("ArchUnit Rule 4: Controllers chỉ được nằm trong package api (Strict check)")
    void rule4_controllersMustResideInApiPackage() {
        ArchRule rule = classes()
                .that().areAnnotatedWith(org.springframework.web.bind.annotation.RestController.class)
                .should().resideInAPackage("..api..")
                .as("All REST controllers must reside in api packages");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("ArchUnit Rule 5: Repositories của JPA phải nằm trong persistence/infrastructure (Warn mode for Phase 2)")
    void rule5_repositoriesMustResideInInfrastructure() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Repository")
                .and().areInterfaces()
                .should().resideInAnyPackage("..infrastructure..", "..persistence..", "..domain..")
                .as("Repository interfaces should reside in persistence, infrastructure or domain port packages");

        rule.check(importedClasses);
    }
}
