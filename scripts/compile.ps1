param(
    [string]$BuildDir = "target\classes"
)

$ErrorActionPreference = "Stop"

$ProjectRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
Push-Location $ProjectRoot
try {
    New-Item -ItemType Directory -Force -Path $BuildDir | Out-Null

    $sources = @()
    $sources += Get-ChildItem -Path "src\main\java" -Filter "*.java" -Recurse | ForEach-Object { $_.FullName }
    if (Test-Path "src\test\java") {
        $sources += Get-ChildItem -Path "src\test\java" -Filter "*.java" -Recurse | ForEach-Object { $_.FullName }
    }

    if ($sources.Count -eq 0) {
        throw "No Java source files found under src\main\java."
    }

    javac -encoding UTF-8 -d $BuildDir $sources
    Write-Host "Compiled application and tests into $BuildDir"
}
finally {
    Pop-Location
}
