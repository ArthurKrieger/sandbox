# Loads .env into the current PowerShell session. Dot-source it: . .\scripts\load-env.ps1
if (-not (Test-Path "./.env")) { throw ".env file not found in current directory" }
Get-Content .\.env | ForEach-Object {
  $line = $_.Trim()
  if ($line -and -not $line.StartsWith('#')) {
    $parts = $line -split '=', 2
    if ($parts.Count -eq 2 -and $parts[0].Trim() -ne '') {
      Set-Item -Path "Env:$($parts[0].Trim())" -Value $parts[1].Trim()
    }
  }
}
Write-Output "Loaded .env into process environment"
