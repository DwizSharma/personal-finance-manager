package com.financemanager.config;

import com.financemanager.entity.Category;
import com.financemanager.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeds the default income/expense categories on startup (spring.sql.init.mode=never,
 * so no SQL script does this). Idempotent: a type that already has categories is left alone.
 */
@Component
@Order(1)
public class CategorySeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CategorySeeder.class);

    private final CategoryRepository categoryRepository;

    public CategorySeeder(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {
        seed(Category.Type.INCOME, List.of("Salary", "Freelance", "Other Income"));
        seed(Category.Type.EXPENSE, List.of("Groceries", "Rent", "Utilities", "Entertainment",
                "Transport", "Healthcare", "Miscellaneous"));
    }

    private void seed(Category.Type type, List<String> names) {
        if (!categoryRepository.findByType(type).isEmpty()) {
            return;
        }
        for (String name : names) {
            categoryRepository.save(new Category(name, type));
        }
        log.info("Seeded {} {} categories.", names.size(), type);
    }
}
