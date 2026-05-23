param(
    [string]$BuildDir = "target\classes"
)

$ErrorActionPreference = "Stop"

$ProjectRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
& "$PSScriptRoot\compile.ps1" -BuildDir $BuildDir
Push-Location $ProjectRoot
try {
    java -cp $BuildDir app.PrototypeTests
}
finally {
    Pop-Location
}
