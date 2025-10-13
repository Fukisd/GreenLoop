@echo off
echo Fixing BOM characters in Java files...

REM Get list of Java files with BOM issues
for /r src %%f in (*.java) do (
    echo Processing %%f
    powershell -Command "& {$content = Get-Content '%%f' -Raw -Encoding UTF8; if ($content.StartsWith([char]0xFEFF)) { $content = $content.Substring(1); Set-Content '%%f' -Value $content -Encoding UTF8 -NoNewline; Write-Host 'Fixed BOM in %%f' } else { Write-Host 'No BOM found in %%f' }}"
)

echo BOM fix completed!
pause

