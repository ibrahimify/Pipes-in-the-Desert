param(
    [string]$BuildDir = "target\classes"
)

$ErrorActionPreference = "Stop"

New-Item -ItemType Directory -Force -Path $BuildDir | Out-Null

$sources = @()
$sources += Get-ChildItem -Path "src\main\java\app" -Filter "*.java" | ForEach-Object { $_.FullName }
if (Test-Path "src\test\java\app") {
    $sources += Get-ChildItem -Path "src\test\java\app" -Filter "*.java" | ForEach-Object { $_.FullName }
}

javac -d $BuildDir $sources
Write-Host "Compiled application and tests into $BuildDir"
