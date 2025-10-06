#!/bin/bash

# Railway Deployment Script for Green Loop Backend
# This script helps prepare and deploy the application to Railway

echo "🚀 Starting Railway deployment for Green Loop Backend..."

# Check if Railway CLI is installed
if ! command -v railway &> /dev/null; then
    echo "❌ Railway CLI is not installed. Please install it first:"
    echo "   npm install -g @railway/cli"
    echo "   or visit: https://docs.railway.app/develop/cli"
    exit 1
fi

# Check if user is logged in to Railway
if ! railway whoami &> /dev/null; then
    echo "🔐 Please log in to Railway first:"
    echo "   railway login"
    exit 1
fi

echo "✅ Railway CLI is installed and user is logged in"

# Build the application
echo "🔨 Building the application..."
./mvnw clean package -DskipTests -Pprod

if [ $? -ne 0 ]; then
    echo "❌ Build failed. Please check the errors above."
    exit 1
fi

echo "✅ Application built successfully"

# Deploy to Railway
echo "🚀 Deploying to Railway..."
railway up

if [ $? -eq 0 ]; then
    echo "✅ Deployment successful!"
    echo "🌐 Your application should be available at the Railway URL"
    echo "📊 Check the Railway dashboard for logs and monitoring"
else
    echo "❌ Deployment failed. Please check the errors above."
    exit 1
fi

echo "🎉 Railway deployment completed!"
