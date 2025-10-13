# 🚂 Railway Deployment Guide for Green Loop Backend

## 📋 Prerequisites

1. **Railway Account**: Sign up at https://railway.app
2. **Railway CLI**: Install the CLI tool
3. **PostgreSQL Database**: Create on Railway
4. **Git Repository**: Code pushed to GitHub

---

## 🚀 Quick Deployment Steps

### Step 1: Install Railway CLI

```bash
npm install -g @railway/cli
```

### Step 2: Login to Railway

```bash
railway login
```

This will open a browser window to authenticate.

### Step 3: Link to Your Railway Project

**Option A: Create New Project**
```bash
cd D:\text\ver2\group3\group2\green-loop-be
railway init
```

**Option B: Link Existing Project**
```bash
railway link
```

### Step 4: Add PostgreSQL Database

**In Railway Dashboard:**
1. Go to your project
2. Click "+ New"
3. Select "Database" → "PostgreSQL"
4. Wait for provisioning (~30 seconds)

**Or via CLI:**
```bash
railway add --database postgresql
```

### Step 5: Set Environment Variables

**In Railway Dashboard:**
1. Go to your service
2. Click "Variables" tab
3. Add these variables:

```env
# Database (Auto-set by Railway PostgreSQL)
DATABASE_URL=postgresql://...  (Already set by Railway)

# JWT Configuration
JWT_SECRET=mySecretKey1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()

# Cloudinary
CLOUDINARY_CLOUD_NAME=dmpjc496u
CLOUDINARY_API_KEY=867162548936863
CLOUDINARY_API_SECRET=t_Wp6_Yoc8xLv0nXXfqO-gIVF8I

# Email (Gmail SMTP)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=greenloopcyc@gmail.com
MAIL_PASSWORD=mubkcunbpryolcpj

# Frontend URL (update after deploying frontend)
FRONTEND_URL=https://your-frontend-url.vercel.app
CORS_ALLOWED_ORIGINS=https://your-frontend-url.vercel.app

# Optional: Redis (if needed)
# REDIS_HOST=...
# REDIS_PORT=6379
```

### Step 6: Deploy!

**Method A: Deploy via CLI**
```bash
railway up
```

**Method B: Deploy via Git Push**
```bash
git add .
git commit -m "feat: Complete item, brand, category management"
git push
```

Railway will auto-deploy on push if connected to GitHub!

---

## 🔧 Configuration Files

Your backend now has all the necessary Railway files:

### ✅ `Dockerfile.railway`
- Multi-stage build (smaller image)
- Maven build stage
- Production JRE image
- Non-root user for security
- Dynamic PORT support

### ✅ `railway.json`
- Specifies Dockerfile builder
- Health check configuration
- Restart policy

### ✅ `Procfile`
- Start command with dynamic PORT

### ✅ `nixpacks.toml`
- Alternative build configuration

---

## 🐛 Troubleshooting

### Issue: Build Fails with "Deploy failed"

**Cause:** Maven dependencies download timeout or network issues

**Solution:**
1. Railway has 10-minute build timeout
2. Check Railway logs: `railway logs`
3. Try deploying again (cached layers should help)

### Issue: "Address already in use"

**Cause:** Not using Railway's PORT variable

**Solution:** Already fixed! Dockerfile now uses `$PORT`

### Issue: Database connection fails

**Cause:** DATABASE_URL not set or incorrect format

**Solution:**
1. Check Railway PostgreSQL plugin is installed
2. Verify DATABASE_URL in environment variables
3. Format should be: `postgresql://user:pass@host:port/dbname`

### Issue: 403 or 401 errors after deployment

**Cause:** CORS or JWT secret not configured

**Solution:**
1. Set `FRONTEND_URL` environment variable
2. Set `CORS_ALLOWED_ORIGINS` to match your frontend
3. Set `JWT_SECRET` (must be 64+ characters for HS512)

---

## 📊 After Deployment

### 1. Get Your URLs

```bash
railway domain
```

This shows your deployed URL, e.g.: `https://your-app.up.railway.app`

### 2. Check Logs

```bash
railway logs
```

### 3. Test Endpoints

```bash
# Health check
curl https://your-app.up.railway.app/actuator/health

# API test
curl https://your-app.up.railway.app/api/brands
```

### 4. View Swagger Docs

```
https://your-app.up.railway.app/swagger-ui.html
```

---

## ⚙️ Railway Environment Variables Checklist

### Required:
- ✅ `DATABASE_URL` - Auto-set by PostgreSQL plugin
- ✅ `JWT_SECRET` - 64+ characters
- ✅ `FRONTEND_URL` - Your deployed frontend URL
- ✅ `CORS_ALLOWED_ORIGINS` - Same as FRONTEND_URL

### Optional:
- `CLOUDINARY_CLOUD_NAME`
- `CLOUDINARY_API_KEY`
- `CLOUDINARY_API_SECRET`
- `MAIL_HOST`
- `MAIL_PORT`
- `MAIL_USERNAME`
- `MAIL_PASSWORD`

### Auto-set by Railway:
- `PORT` - Railway dynamically assigns this
- `RAILWAY_ENVIRONMENT`
- `RAILWAY_PROJECT_ID`

---

## 🎯 Quick Deploy Command

```bash
# One-line deploy (from backend directory)
cd D:\text\ver2\group3\group2\green-loop-be && railway up
```

---

## 🔍 Common Railway Deploy Errors

### "Failed to fetch dependencies"
- Network timeout during Maven dependency download
- **Solution:** Re-deploy, Railway will use cached layers

### "Port 8080 already in use"
- Not using Railway's PORT variable
- **Solution:** Already fixed in our Dockerfile!

### "Health check failed"
- Health endpoint not responding
- **Solution:** Updated to use `/actuator/health`

### "Database connection refused"
- PostgreSQL not provisioned
- **Solution:** Add PostgreSQL plugin in Railway dashboard

---

## 📝 Deployment Checklist

Before deploying:
- ✅ All code committed to Git
- ✅ `.gitignore` excludes `target/`, `.env`, sensitive files
- ✅ `Dockerfile.railway` exists
- ✅ `railway.json` configured
- ✅ Environment variables set in Railway
- ✅ PostgreSQL database added to project

---

## 🎉 Your Backend is Railway-Ready!

All configuration files are updated and optimized for Railway deployment!

Run this from your backend directory:
```bash
railway up
```

Or connect to GitHub and Railway will auto-deploy on push! 🚀






