@echo off
echo ========================================
echo Hibernate Tools - Entity Generation
echo ========================================
echo.

echo [1/3] Cleaning previous generated files...
call mvn clean

echo.
echo [2/3] Generating entities from database...
call mvn hibernate-tools:generate

echo.
echo [3/3] Compiling generated entities...
call mvn compile

echo.
echo ========================================
echo Entity generation completed!
echo ========================================
echo.
echo Generated entities are located in:
echo target/generated-sources/hibernate-tools/org/greenloop/circularfashion/entity/generated/
echo.
echo You can now copy the generated entities to:
echo src/main/java/org/greenloop/circularfashion/entity/
echo.
pause
