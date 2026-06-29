param(
    [string]$ApiUrl = "http://localhost:8080"
)

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $MyInvocation.MyCommand.Path

Write-Host "Build backend..." -ForegroundColor Cyan
mvn -q -DskipTests package

Write-Host "Avvio backend..." -ForegroundColor Cyan
$backend = Start-Process -FilePath java -ArgumentList "-jar `"$root\target\restaurant.jar`" --restaurant.demo.reset-enabled=true" -NoNewWindow -PassThru

try {
    Write-Host "Attendo il backend..." -ForegroundColor Cyan
    $ready = $false
    for ($i = 0; $i -lt 30; $i++) {
        try {
            $null = Invoke-RestMethod -Uri "$ApiUrl/api/config" -TimeoutSec 2
            $ready = $true
            break
        } catch {
            Start-Sleep -Seconds 1
        }
    }
    if (-not $ready) {
        throw "Backend non avviato dopo 30 secondi"
    }

    Write-Host "Backend pronto. Avvio TUI..." -ForegroundColor Cyan
    $env:RESTAURANT_API_URL = $ApiUrl
    go run -C $root\tui .\cmd\restaurant-tui
} finally {
    Write-Host "Arresto backend..." -ForegroundColor Cyan
    Stop-Process -Id $backend.Id -Force -ErrorAction SilentlyContinue
}
