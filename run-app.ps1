$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$buildRoot = Join-Path $projectRoot "build"
$classesDir = Join-Path $buildRoot "classes"
$pidFile = Join-Path $buildRoot "server.pid"
$stdoutLog = Join-Path $buildRoot "server.out.log"
$stderrLog = Join-Path $buildRoot "server.err.log"
$javawPath = if ($env:JAVA_HOME) { Join-Path $env:JAVA_HOME "bin\javaw.exe" } else { $null }
$port = 8081

New-Item -ItemType Directory -Force -Path $buildRoot, $classesDir | Out-Null
if (Test-Path $classesDir) {
    Get-ChildItem -Path $classesDir -Force | Remove-Item -Recurse -Force
}

$sources = @()
$sources += Get-ChildItem -Path (Join-Path $projectRoot "src\main\java\com\wedding\model") -Recurse -Filter *.java | ForEach-Object { $_.FullName }
$sources += Get-ChildItem -Path (Join-Path $projectRoot "src\main\java\com\wedding\local") -Recurse -Filter *.java | ForEach-Object { $_.FullName }

foreach ($source in $sources) {
    if (-not (Test-Path $source)) {
        throw "Source file not found: $source"
    }
}

& javac --add-modules jdk.httpserver -encoding UTF-8 -d $classesDir $sources
if ($LASTEXITCODE -ne 0) {
    throw "Compilation failed."
}

if (Test-Path $pidFile) {
    $oldPid = (Get-Content $pidFile | Select-Object -First 1).Trim()
    if ($oldPid) {
        $existingProcess = Get-Process -Id $oldPid -ErrorAction SilentlyContinue
        if ($existingProcess) {
            Stop-Process -Id $oldPid -Force
            Start-Sleep -Seconds 1
        }
    }
}

$localServerProcesses = Get-CimInstance Win32_Process -Filter "Name = 'java.exe'" | Where-Object {
    $_.CommandLine -like "*com.wedding.local.LocalWeddingServer*"
}

foreach ($processInfo in $localServerProcesses) {
    Stop-Process -Id $processInfo.ProcessId -Force -ErrorAction SilentlyContinue
}

if ($localServerProcesses) {
    Start-Sleep -Seconds 1
}

$javaExecutable = if (Test-Path $javawPath) { $javawPath } else { "javaw" }
$javaArgs = @("--add-modules", "jdk.httpserver", "-cp", """$classesDir""", "com.wedding.local.LocalWeddingServer", "$port")
$process = Start-Process -FilePath $javaExecutable -ArgumentList $javaArgs -WorkingDirectory $projectRoot -PassThru -WindowStyle Hidden
Set-Content -Path $pidFile -Value $process.Id -Encoding ASCII

$isReady = $false
for ($attempt = 0; $attempt -lt 20; $attempt++) {
    Start-Sleep -Milliseconds 500
    if ($process.HasExited) {
        break
    }

    try {
        $response = Invoke-WebRequest -Uri "http://localhost:$port/login" -UseBasicParsing -TimeoutSec 2
        if ($response.StatusCode -eq 200 -and $response.Content -match "Wedding Planning and Vendor Booking System") {
            $isReady = $true
            break
        }
    } catch {
    }
}

if (-not $isReady) {
    if ($process.HasExited) {
        throw "Server failed to start."
    }
    throw "Server started but did not become ready on port $port."
}

Write-Host "Server running with PID $($process.Id)"
Write-Host "App URL: http://localhost:$port/login"
