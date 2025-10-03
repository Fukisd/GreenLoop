package org.greenloop.circularfashion.controller;

import org.greenloop.circularfashion.util.DatabaseSchemaExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class SchemaExportController {

    @Autowired
    private DatabaseSchemaExporter schemaExporter;

    @GetMapping("/export-schema")
    public String exportSchema() {
        schemaExporter.exportSchema();
        return "Schema exported to console. Check the application logs.";
    }
}
