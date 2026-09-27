# Load .env into current PowerShell session (do not use in CI)
# Usage: .\load-env.ps1  (dot-source to keep vars in current session)
if (-Not (Test-Path "./.env")) { Write-Error ".env file not found in current directory"; exit 1 }
Get-Content .\.env | ForEach-Object {
  $line = $_.Trim()
  if ($line -and -not $line.StartsWith('#')) {
    $parts = $line -split '=',2
    if ($parts.Count -eq 2) {
      $name = $parts[0].Trim()
      $value = $parts[1].Trim()
      if ($name -ne '') { Set-Item -Path "Env:$name" -Value $value }
    }
  }
}
Write-Output "Loaded .env into process environment (use dot-sourcing: . ./load-env.ps1)"