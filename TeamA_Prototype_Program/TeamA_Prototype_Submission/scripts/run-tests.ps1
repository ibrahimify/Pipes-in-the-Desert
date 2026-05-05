param(
    [string]$BuildDir = "build\classes"
)

$ErrorActionPreference = "Stop"

& "$PSScriptRoot\compile.ps1" -BuildDir $BuildDir
java -cp $BuildDir app.PrototypeTests
