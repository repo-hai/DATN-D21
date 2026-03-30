package com.DATN.Bej.config;

import java.util.HashSet;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.DATN.Bej.entity.product.Category;
import com.DATN.Bej.repository.RoleRepository;
import com.DATN.Bej.repository.UserRepository;
import com.DATN.Bej.repository.product.CategoryRepository;

import lombok.AllArgsConstructor;


@Component
@AllArgsConstructor
public class DatabaseInitlizer implements CommandLineRunner{

    @Autowired
    private final JdbcTemplate jdbcTemplate;
    
    @Autowired
    private final DataSource dataSource;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final RoleRepository roleRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== Checking Database Initialization ===");
        
        if (isDatabaseEmpty()) {
            System.out.println("Database is empty. Running SQL scripts...");
            //executeSqlScript("schema.sql");
            executeSqlScript("data.sql");
            
            //verifyDataInsertion();
            System.out.println("=== Database Initialization Completed ===");
        }
        
        // Initialize categories nếu chưa có
        initializeCategories();
    }
    
    /**
     * Khởi tạo các categories cơ bản nếu chưa có trong database
     */
    private void initializeCategories() {
        System.out.println("=== Initializing Categories ===");
        
        // Danh sách categories cần có
        String[][] categories = {
            {"Điện thoại", "DT"},
            {"Phụ kiện", "PK"},
            {"Sửa chữa", "SC"}
        };
        
        int createdCount = 0;
        int existingCount = 0;
        
        for (String[] categoryData : categories) {
            String name = categoryData[0];
            String sku = categoryData[1];
            
            // Kiểm tra category đã tồn tại chưa
            if (!categoryRepository.existsByName(name)) {
                Category category = new Category();
                category.setName(name);
                category.setSku(sku);
                categoryRepository.save(category);
                System.out.println("✅ Created category: " + name + " (SKU: " + sku + ")");
                createdCount++;
            } else {
                System.out.println("⏭️  Category already exists: " + name);
                existingCount++;
            }
        }
        
        System.out.println("=== Category Initialization Summary ===");
        System.out.println("Created: " + createdCount);
        System.out.println("Already exists: " + existingCount);
        System.out.println("Total categories: " + categoryRepository.count());
    }

    private boolean isDatabaseEmpty() {
        try {
            Integer productCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM product", Integer.class);
            
            System.out.println("Existing products count: " + productCount);
            
            // Chỉ seed data nếu KHÔNG có sản phẩm
            return (productCount == null || productCount == 0);
            
        } catch (Exception e) {
            System.out.println("Tables don't exist or error checking database. Assuming empty.");
            return true;
        }
    }

    private void verifyDataInsertion() {
        try {
            Integer roleCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM roles", Integer.class);
            Integer userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
            Integer leadCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM leads", Integer.class);
            
            System.out.println("Verification Results:");
            System.out.println("- Roles inserted: " + (roleCount != null ? roleCount : 0));
            System.out.println("- Users inserted: " + (userCount != null ? userCount : 0));
            System.out.println("- Leads inserted: " + (leadCount != null ? leadCount : 0));
            
            if ((roleCount == null || roleCount == 0) || 
                (userCount == null || userCount == 0)) {
                System.err.println("Data insertion seems to have failed!");
            } else {
                System.out.println("Data insertion successful!");
            }
        } catch (Exception e) {
            System.err.println("Error verifying data insertion: " + e.getMessage());
        }
    }

    private void executeSqlScript(String scriptPath) {
        try {
            System.out.println("Executing " + scriptPath + "...");
            
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new ClassPathResource(scriptPath));
            populator.setContinueOnError(true);
            populator.setIgnoreFailedDrops(true);
            populator.execute(dataSource);
            
            System.out.println("Successfully executed " + scriptPath);
        } catch (Exception e) {
            System.err.println("Error executing " + scriptPath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}