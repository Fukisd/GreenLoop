# Comprehensive BOM Character Fix Script
Write-Host "Starting comprehensive BOM character fix..." -ForegroundColor Green

# Get all Java files recursively
$javaFiles = Get-ChildItem -Path "src" -Recurse -Filter "*.java"

$fixedCount = 0
$totalCount = $javaFiles.Count

Write-Host "Found $totalCount Java files to process..." -ForegroundColor Yellow

foreach ($file in $javaFiles) {
    try {
        # Read file content as bytes to detect BOM
        $content = [System.IO.File]::ReadAllBytes($file.FullName)
        
        # Check if file starts with UTF-8 BOM (EF BB BF)
        if ($content.Length -ge 3 -and $content[0] -eq 0xEF -and $content[1] -eq 0xBB -and $content[2] -eq 0xBF) {
            Write-Host "Fixing BOM in: $($file.Name)" -ForegroundColor Cyan
            
            # Remove BOM by reading as UTF-8 and writing without BOM
            $textContent = [System.IO.File]::ReadAllText($file.FullName, [System.Text.Encoding]::UTF8)
            [System.IO.File]::WriteAllText($file.FullName, $textContent, [System.Text.UTF8Encoding]::new($false))
            
            $fixedCount++
        }
    }
    catch {
        Write-Host "Error processing $($file.Name): $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host "BOM fix completed!" -ForegroundColor Green
Write-Host "Fixed $fixedCount out of $totalCount files" -ForegroundColor Yellow

# Also fix any other common encoding issues
Write-Host "Checking for other encoding issues..." -ForegroundColor Yellow

# Fix any files that might have Windows-1252 encoding
foreach ($file in $javaFiles) {
    try {
        $content = [System.IO.File]::ReadAllText($file.FullName, [System.Text.Encoding]::UTF8)
        # Check if content contains any non-ASCII characters that might be problematic
        if ($content -match '[^\x00-\x7F]') {
            # Re-encode the file properly
            [System.IO.File]::WriteAllText($file.FullName, $content, [System.Text.UTF8Encoding]::new($false))
        }
    }
    catch {
        Write-Host "Error re-encoding $($file.Name): $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host "All encoding fixes completed!" -ForegroundColor Green

