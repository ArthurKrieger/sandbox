package dev.arthur.sandbox;

import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.base.DescribedPredicate.alwaysTrue;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = "dev.arthur.sandbox", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    private static final String DOMAIN = "dev.arthur.sandbox..domain..";
    private static final String APPLICATION = "dev.arthur.sandbox..application..";
    private static final String PRESENTATION = "dev.arthur.sandbox..presentation..";
    private static final String INFRA = "dev.arthur.sandbox..infra..";
    private static final String PERSISTENCE = "dev.arthur.sandbox..infra.persistence..";
    private static final String PORTS = "dev.arthur.sandbox..application.port..";

    // Annotations and assertions only: none of these pull a framework into the core's behaviour.
    private static final String[] CORE_LIBRARIES = {"java..", "lombok..", "org.jspecify..",
            "org.springframework.util.."};

    @ArchTest
    static final ArchRule verticalsDoNotDependOnEachOther = slices()
            .matching("dev.arthur.sandbox.(*)..")
            .should().notDependOnEachOther()
            .ignoreDependency(alwaysTrue(), resideInAPackage("dev.arthur.sandbox.shared.."));

    @ArchTest
    static final ArchRule domainDependsOnNothingButItself = classes()
            .that().resideInAPackage(DOMAIN)
            .should().onlyDependOnClassesThat().resideInAnyPackage(concat(CORE_LIBRARIES, DOMAIN));

    @ArchTest
    static final ArchRule applicationDependsOnlyOnTheDomain = classes()
            .that().resideInAPackage(APPLICATION)
            .should().onlyDependOnClassesThat().resideInAnyPackage(concat(CORE_LIBRARIES, DOMAIN, APPLICATION,
                    "org.springframework.transaction.annotation..", "org.slf4j.."));

    @ArchTest
    static final ArchRule presentationGoesThroughTheApplication = noClasses()
            .that().resideInAPackage(PRESENTATION)
            .should().dependOnClassesThat().haveSimpleNameEndingWith("Repository")
            .orShould().dependOnClassesThat().resideInAPackage(PERSISTENCE);

    @ArchTest
    static final ArchRule infraDoesNotReachPresentation = noClasses()
            .that().resideInAPackage(INFRA)
            .should().dependOnClassesThat().resideInAPackage(PRESENTATION);

    @ArchTest
    static final ArchRule repositoriesInTheCoreAreInterfaces = classes()
            .that().resideInAnyPackage(DOMAIN, APPLICATION)
            .and().haveSimpleNameEndingWith("Repository")
            .should().beInterfaces();

    @ArchTest
    static final ArchRule portsAreInterfaces = classes()
            .that().resideInAPackage(PORTS)
            .and().areTopLevelClasses()
            .and().areNotEnums()
            .and().areNotRecords()
            .and().areNotAssignableTo(Exception.class)
            .should().beInterfaces();

    @ArchTest
    static final ArchRule fieldsAreFinal = fields()
            .that().areDeclaredInClassesThat().resideInAPackage("dev.arthur.sandbox..")
            .and().doNotHaveModifier(JavaModifier.VOLATILE)
            .should().beFinal();

    private static String[] concat(String[] first, String... rest) {
        final String[] all = new String[first.length + rest.length];
        System.arraycopy(first, 0, all, 0, first.length);
        System.arraycopy(rest, 0, all, first.length, rest.length);
        return all;
    }
}
