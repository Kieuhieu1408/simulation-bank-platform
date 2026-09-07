package com.hieu.moneybank.architecture;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Cưỡng chế chiều dependency trong {@code components.md} mục 6.
 *
 * <p>Không có test này, quy tắc kiến trúc chỉ là văn bản: một lần import tiện tay
 * từ {@code domain} sang JPA hoặc Spring Web là đủ để domain bị khóa vào hạ tầng,
 * và nó sẽ không bị ai phát hiện trong review.
 *
 * <p>Test đọc trực tiếp source thay vì dùng thư viện phân tích bytecode để không
 * thêm dependency mới ở giai đoạn này. Đổi lại, nó chỉ kiểm tra được câu lệnh
 * import, đủ cho các quy tắc đang có.
 */
class DependencyDirectionTest {

    private static final Path MAIN_SOURCE_ROOT = Path.of("src", "main", "java");
    private static final Pattern PACKAGE_PATTERN = Pattern.compile("^package\\s+([\\w.]+);", Pattern.MULTILINE);
    private static final Pattern IMPORT_PATTERN = Pattern.compile("^import\\s+(?:static\\s+)?([\\w.]+)", Pattern.MULTILINE);

    /** Hạ tầng mà domain không được biết tới. */
    private static final List<String> INFRASTRUCTURE_PACKAGES = List.of(
            "jakarta.persistence",
            "jakarta.servlet",
            "org.hibernate",
            "org.springframework.data",
            "org.springframework.web",
            "org.springframework.http",
            "org.springframework.transaction",
            "org.springframework.security",
            "io.micrometer");

    private record JavaSource(Path path, String packageName, List<String> imports) {
    }

    /** Quy ước package của SRD mục 7: {@code <context>.domain[...]}. */
    private boolean isDomain(String packageName) {
        return packageName.endsWith(".domain") || packageName.contains(".domain.");
    }

    /** Quy ước package của SRD mục 7: {@code <context>.api[...]}. */
    private boolean isApi(String packageName) {
        return packageName.endsWith(".api") || packageName.contains(".api.");
    }

    private List<JavaSource> mainSources() throws IOException {
        try (Stream<Path> files = Files.walk(MAIN_SOURCE_ROOT)) {
            List<JavaSource> sources = new ArrayList<>();
            for (Path path : files.filter(p -> p.toString().endsWith(".java")).toList()) {
                String content = Files.readString(path, StandardCharsets.UTF_8);
                sources.add(new JavaSource(path, extractPackage(content), extractImports(content)));
            }
            return sources;
        }
    }

    private String extractPackage(String content) {
        Matcher matcher = PACKAGE_PATTERN.matcher(content);
        return matcher.find() ? matcher.group(1) : "";
    }

    private List<String> extractImports(String content) {
        Matcher matcher = IMPORT_PATTERN.matcher(content);
        List<String> imports = new ArrayList<>();
        while (matcher.find()) {
            imports.add(matcher.group(1));
        }
        return imports;
    }

    @Test
    @DisplayName("Có source chính để kiểm tra")
    void sourceSetIsNotEmpty() throws IOException {
        // Bảo vệ chính test này: nếu đường dẫn sai, các test dưới sẽ pass rỗng và
        // tạo cảm giác an toàn giả.
        assertThat(mainSources()).isNotEmpty();
    }

    @Test
    @DisplayName("domain không phụ thuộc hạ tầng")
    void domainDoesNotDependOnInfrastructure() throws IOException {
        List<String> violations = new ArrayList<>();
        for (JavaSource source : mainSources()) {
            if (!isDomain(source.packageName())) {
                continue;
            }
            for (String imported : source.imports()) {
                boolean forbidden = INFRASTRUCTURE_PACKAGES.stream().anyMatch(imported::startsWith)
                        || imported.contains(".infrastructure.");
                if (forbidden) {
                    violations.add(source.path() + " -> " + imported);
                }
            }
        }
        assertThat(violations)
                .as("domain phải độc lập với database, web và messaging")
                .isEmpty();
    }

    @Test
    @DisplayName("api không phụ thuộc trực tiếp domain")
    void apiDoesNotDependOnDomainDirectly() throws IOException {
        List<String> violations = new ArrayList<>();
        for (JavaSource source : mainSources()) {
            if (!isApi(source.packageName())) {
                continue;
            }
            for (String imported : source.imports()) {
                if (imported.contains(".domain.")) {
                    violations.add(source.path() + " -> " + imported);
                }
            }
        }
        assertThat(violations)
                .as("controller chỉ điều phối transport, không chạm domain")
                .isEmpty();
    }

    @Test
    @DisplayName("Source chính không phụ thuộc thư viện test")
    void mainDoesNotDependOnTestLibraries() throws IOException {
        List<String> violations = new ArrayList<>();
        for (JavaSource source : mainSources()) {
            for (String imported : source.imports()) {
                if (imported.startsWith("org.junit")
                        || imported.startsWith("org.assertj")
                        || imported.startsWith("org.mockito")
                        || imported.startsWith("org.h2")) {
                    violations.add(source.path() + " -> " + imported);
                }
            }
        }
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Không dùng System.out/System.err thay cho structured log")
    void noConsolePrinting() throws IOException {
        List<String> violations = new ArrayList<>();
        try (Stream<Path> files = Files.walk(MAIN_SOURCE_ROOT)) {
            for (Path path : files.filter(p -> p.toString().endsWith(".java")).toList()) {
                String content = Files.readString(path, StandardCharsets.UTF_8);
                if (content.contains("System.out.print") || content.contains("System.err.print")) {
                    // In ra console bỏ qua toàn bộ masking và structured field, nên
                    // vừa mất khả năng truy vết vừa có nguy cơ rò dữ liệu.
                    violations.add(path.toString());
                }
            }
        }
        assertThat(violations).isEmpty();
    }
}
