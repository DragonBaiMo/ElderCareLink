@echo off
REM =============================
REM 关爱空巢老人项目 - Windows 一键建表脚本
REM 使用环境变量控制连接参数，默认账户 root/root，数据库 eldercare
REM 必须提前启动 MySQL 服务
REM =============================

setlocal enabledelayedexpansion

REM 读取连接配置，支持覆盖默认值
set "MYSQL_HOST=%MYSQL_HOST%"
if "!MYSQL_HOST!"=="" set "MYSQL_HOST=127.0.0.1"
set "MYSQL_PORT=%MYSQL_PORT%"
if "!MYSQL_PORT!"=="" set "MYSQL_PORT=3306"
set "MYSQL_USER=%MYSQL_USER%"
if "!MYSQL_USER!"=="" set "MYSQL_USER=root"
set "MYSQL_PASSWORD=%MYSQL_PASSWORD%"
if "!MYSQL_PASSWORD!"=="" set "MYSQL_PASSWORD=root"

REM 支持通过 MYSQL_HOME 指定 mysql.exe 位置，否则依赖 PATH
if not "%MYSQL_HOME%"=="" (
  set "MYSQL_BIN=%MYSQL_HOME%\bin\mysql.exe"
) else (
  set "MYSQL_BIN=mysql"
)

set "SCHEMA_PATH=%~dp0..\schema.sql"

echo 即将导入数据表...
echo 目标: !MYSQL_HOST!:!MYSQL_PORT! 用户: !MYSQL_USER! 数据库: eldercare

"!MYSQL_BIN!" -h!MYSQL_HOST! -P!MYSQL_PORT! -u!MYSQL_USER! -p!MYSQL_PASSWORD! --default-character-set=utf8mb4 -e "CREATE DATABASE IF NOT EXISTS eldercare DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
if %ERRORLEVEL% neq 0 (
  echo 无法创建数据库，请检查 MySQL 服务或账号密码。
  exit /b 1
)

"!MYSQL_BIN!" -h!MYSQL_HOST! -P!MYSQL_PORT! -u!MYSQL_USER! -p!MYSQL_PASSWORD! --default-character-set=utf8mb4 eldercare < "!SCHEMA_PATH!"
if %ERRORLEVEL% neq 0 (
  echo 导入失败，请确认 MySQL 已安装且 schema.sql 路径正确。
  exit /b 1
)

echo 导入完成，可直接启动后端连接 eldercare 数据库。
endlocal
