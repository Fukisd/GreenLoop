@echo off
REM Railway Deployment Script for Green Loop Backend
REM This script helps prepare and deploy the application to Railway

echo 🚀 Starting Railway deployment for Green Loop Backend...

REM Check if Railway CLI is installed
railway --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Railway CLI is not installed. Please install it first:
    echo    npm install -g @railway/cli
    echo    or visit: https://docs.railway.app/develop/cli
    pause
    exit /b 1
)

REM Check if user is logged in to Railway
railway whoami >nul 2>&1
if errorlevel 1 (
    echo 🔐 Please log in to Railway first:
    echo    railway login
    pause
    exit /b 1
)

echo ✅ Railway CLI is installed and user is logged in

REM Build the application
echo 🔨 Building the application...
call mvnw clean package -DskipTests -Pprod

if errorlevel 1 (
    echo ❌ Build failed. Please check the errors above.
    pause
    exit /b 1
)

echo ✅ Application built successfully

REM Deploy to Railway
echo 🚀 Deploying to Railway...
railway up

if errorlevel 1 (
    echo ❌ Deployment failed. Please check the errors above.
    pause
    exit /b 1
)

echo ✅ Deployment successful!
echo 🌐 Your application should be available at the Railway URL
echo 📊 Check the Railway dashboard for logs and monitoring
echo 🎉 Railway deployment completed!
pause

