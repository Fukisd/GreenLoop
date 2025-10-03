package org.greenloop.circularfashion.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DatabaseSchemaExporter {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void exportSchema() {
        System.out.println("=== DATABASE SCHEMA EXPORT ===");
        
        // Get all tables
        List<Map<String, Object>> tables = jdbcTemplate.queryForList(
            "SELECT table_name, table_type FROM information_schema.tables " +
            "WHERE table_schema = 'public' ORDER BY table_name"
        );
        
        System.out.println("\n=== TABLES ===");
        for (Map<String, Object> table : tables) {
            System.out.println("Table: " + table.get("table_name") + " (" + table.get("table_type") + ")");
        }
        
        // Get columns for each table
        System.out.println("\n=== TABLE COLUMNS ===");
        for (Map<String, Object> table : tables) {
            String tableName = (String) table.get("table_name");
            System.out.println("\n--- " + tableName + " ---");
            
            List<Map<String, Object>> columns = jdbcTemplate.queryForList(
                "SELECT column_name, data_type, is_nullable, column_default, character_maximum_length " +
                "FROM information_schema.columns " +
                "WHERE table_schema = 'public' AND table_name = ? " +
                "ORDER BY ordinal_position",
                tableName
            );
            
            for (Map<String, Object> column : columns) {
                String columnName = (String) column.get("column_name");
                String dataType = (String) column.get("data_type");
                String isNullable = (String) column.get("is_nullable");
                String defaultValue = (String) column.get("column_default");
                Object maxLength = column.get("character_maximum_length");
                
                System.out.println("  " + columnName + " (" + dataType + 
                    (maxLength != null ? "(" + maxLength + ")" : "") + 
                    ", " + (isNullable.equals("YES") ? "nullable" : "not null") + 
                    (defaultValue != null ? ", default: " + defaultValue : "") + ")");
            }
        }
        
        // Get foreign keys
        System.out.println("\n=== FOREIGN KEYS ===");
        List<Map<String, Object>> foreignKeys = jdbcTemplate.queryForList(
            "SELECT tc.table_name, kcu.column_name, ccu.table_name AS foreign_table_name, " +
            "ccu.column_name AS foreign_column_name " +
            "FROM information_schema.table_constraints AS tc " +
            "JOIN information_schema.key_column_usage AS kcu " +
            "ON tc.constraint_name = kcu.constraint_name AND tc.table_schema = kcu.table_schema " +
            "JOIN information_schema.constraint_column_usage AS ccu " +
            "ON ccu.constraint_name = tc.constraint_name AND ccu.table_schema = tc.table_schema " +
            "WHERE tc.constraint_type = 'FOREIGN KEY' AND tc.table_schema = 'public' " +
            "ORDER BY tc.table_name, kcu.column_name"
        );
        
        for (Map<String, Object> fk : foreignKeys) {
            System.out.println(fk.get("table_name") + "." + fk.get("column_name") + 
                " -> " + fk.get("foreign_table_name") + "." + fk.get("foreign_column_name"));
        }
        
        // Get unique constraints
        System.out.println("\n=== UNIQUE CONSTRAINTS ===");
        List<Map<String, Object>> uniqueConstraints = jdbcTemplate.queryForList(
            "SELECT tc.table_name, kcu.column_name " +
            "FROM information_schema.table_constraints AS tc " +
            "JOIN information_schema.key_column_usage AS kcu " +
            "ON tc.constraint_name = kcu.constraint_name AND tc.table_schema = kcu.table_schema " +
            "WHERE tc.constraint_type = 'UNIQUE' AND tc.table_schema = 'public' " +
            "ORDER BY tc.table_name, kcu.column_name"
        );
        
        for (Map<String, Object> uc : uniqueConstraints) {
            System.out.println(uc.get("table_name") + "." + uc.get("column_name") + " (UNIQUE)");
        }
    }
}
