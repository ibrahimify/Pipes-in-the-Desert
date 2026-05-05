param(
    [string]$BuildDir = "build\classes"
)

$ErrorActionPreference = "Stop"

New-Item -ItemType Directory -Force -Path $BuildDir | Out-Null

$sources = @()
$sources += Get-ChildItem -Path "src\app" -Filter "*.java" | ForEach-Object { $_.FullName }
if (Test-Path "tests\app") {
    $sources += Get-ChildItem -Path "tests\app" -Filter "*.java" | ForEach-Object { $_.FullName }
}

javac -d $BuildDir $sources
Write-Host "Compiled application and tests into $BuildDir"
