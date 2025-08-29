# 🚀 Deployment Guide - Banking Risk Platform

This guide covers deploying the Banking Risk Platform to various cloud platforms for live demonstration.

## 🌐 Deployment Options

### 1. Local Development (Current)
```bash
# Start all services locally
docker-compose up -d

# Access at:
# Frontend: http://localhost:5173
# Backend: http://localhost:8080
# ML Service: http://localhost:8000
```

### 2. Cloud Deployment Options

#### Option A: AWS EC2 (Recommended for Demo)
- **Cost**: ~$10-20/month for t3.medium instance
- **Setup Time**: 30 minutes
- **Scalability**: Good for demo purposes

#### Option B: Google Cloud Run
- **Cost**: Pay-per-use (very low for demo)
- **Setup Time**: 45 minutes
- **Scalability**: Excellent auto-scaling

#### Option C: Heroku
- **Cost**: Free tier available
- **Setup Time**: 20 minutes
- **Scalability**: Limited on free tier

## 🐳 AWS EC2 Deployment (Recommended)

### Prerequisites
- AWS Account
- EC2 instance (t3.medium recommended)
- Domain name (optional)

### Step 1: Launch EC2 Instance
```bash
# Connect to your EC2 instance
ssh -i your-key.pem ubuntu@your-ec2-ip

# Update system
sudo apt update && sudo apt upgrade -y

# Install Docker
sudo apt install docker.io docker-compose -y
sudo usermod -aG docker ubuntu
```

### Step 2: Clone and Configure
```bash
# Clone repository
git clone https://github.com/yourusername/banking-risk-platform.git
cd banking-risk-platform

# Configure environment variables
cp docker-compose.yml docker-compose.prod.yml
# Edit docker-compose.prod.yml with production settings
```

### Step 3: Configure Production Settings
```yaml
# docker-compose.prod.yml
version: '3.8'
services:
  frontend:
    build: ./frontend
    ports:
      - "80:80"  # Use nginx for production
    environment:
      - VITE_API_URL=http://your-domain.com/api
      - VITE_ML_URL=http://your-domain.com/ml

  backend:
    build: ./backend
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      # Add your Snowflake credentials here

  ml:
    build: ./ml-service
    ports:
      - "8000:8000"

  db:
    image: postgres:16
    environment:
      POSTGRES_DB: bankdb
      POSTGRES_USER: bank
      POSTGRES_PASSWORD: your-secure-password
    volumes:
      - pgdata:/var/lib/postgresql/data
    ports:
      - "5433:5432"

volumes:
  pgdata:
```

### Step 4: Deploy
```bash
# Build and start services
docker-compose -f docker-compose.prod.yml up -d

# Check status
docker-compose -f docker-compose.prod.yml ps

# View logs
docker-compose -f docker-compose.prod.yml logs -f
```

### Step 5: Configure Nginx (Optional)
```bash
# Install nginx
sudo apt install nginx -y

# Configure nginx
sudo nano /etc/nginx/sites-available/banking-platform
```

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # Frontend
    location / {
        proxy_pass http://localhost:5173;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    # Backend API
    location /api/ {
        proxy_pass http://localhost:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    # ML Service
    location /ml/ {
        proxy_pass http://localhost:8000/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

```bash
# Enable site
sudo ln -s /etc/nginx/sites-available/banking-platform /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

## 🔒 Security Considerations

### 1. Environment Variables
```bash
# Create .env file for sensitive data
cat > .env << EOF
SNOWFLAKE_PASSWORD=your-secure-password
POSTGRES_PASSWORD=your-secure-password
JWT_SECRET=your-jwt-secret
EOF
```

### 2. Firewall Configuration
```bash
# Configure AWS Security Groups
# Allow ports: 22 (SSH), 80 (HTTP), 443 (HTTPS)
# Block: 8080, 8000, 5433 (internal only)
```

### 3. SSL Certificate (Recommended)
```bash
# Install Certbot for Let's Encrypt
sudo apt install certbot python3-certbot-nginx -y

# Get SSL certificate
sudo certbot --nginx -d your-domain.com
```

## 📊 Monitoring and Maintenance

### 1. Health Checks
```bash
# Create monitoring script
cat > monitor.sh << 'EOF'
#!/bin/bash
echo "Checking services..."

# Check frontend
if curl -f http://localhost:5173 > /dev/null 2>&1; then
    echo "✅ Frontend: OK"
else
    echo "❌ Frontend: DOWN"
fi

# Check backend
if curl -f http://localhost:8080/health > /dev/null 2>&1; then
    echo "✅ Backend: OK"
else
    echo "❌ Backend: DOWN"
fi

# Check ML service
if curl -f http://localhost:8000/health > /dev/null 2>&1; then
    echo "✅ ML Service: OK"
else
    echo "❌ ML Service: DOWN"
fi
EOF

chmod +x monitor.sh
```

### 2. Automated Backups
```bash
# Create backup script
cat > backup.sh << 'EOF'
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
docker exec banking-risk-platform_db_1 pg_dump -U bank bankdb > backup_$DATE.sql
echo "Backup created: backup_$DATE.sql"
EOF

chmod +x backup.sh

# Add to crontab for daily backups
echo "0 2 * * * /home/ubuntu/backup.sh" | crontab -
```

### 3. Log Management
```bash
# View logs
docker-compose -f docker-compose.prod.yml logs -f

# Rotate logs
sudo logrotate /etc/logrotate.d/docker
```

## 🚀 Quick Deployment Commands

### One-Command Deployment
```bash
# Complete deployment script
cat > deploy.sh << 'EOF'
#!/bin/bash
echo "🚀 Deploying Banking Risk Platform..."

# Pull latest changes
git pull origin main

# Build and start services
docker-compose -f docker-compose.prod.yml down
docker-compose -f docker-compose.prod.yml build --no-cache
docker-compose -f docker-compose.prod.yml up -d

# Wait for services to start
sleep 30

# Run health checks
./monitor.sh

echo "✅ Deployment complete!"
echo "🌐 Access your application at: http://your-domain.com"
EOF

chmod +x deploy.sh
```

## 📈 Performance Optimization

### 1. Resource Limits
```yaml
# Add to docker-compose.prod.yml
services:
  backend:
    deploy:
      resources:
        limits:
          memory: 1G
          cpus: '0.5'
    restart: unless-stopped

  ml:
    deploy:
      resources:
        limits:
          memory: 2G
          cpus: '1.0'
    restart: unless-stopped
```

### 2. Database Optimization
```sql
-- Run in PostgreSQL
CREATE INDEX idx_loan_applications_created_at ON loan_applications(created_at);
CREATE INDEX idx_loan_applications_decision ON loan_applications(decision);
```

## 🔄 CI/CD Pipeline (Optional)

### GitHub Actions
```yaml
# .github/workflows/deploy.yml
name: Deploy to EC2

on:
  push:
    branches: [ main ]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v2
    
    - name: Deploy to EC2
      uses: appleboy/ssh-action@v0.1.5
      with:
        host: ${{ secrets.HOST }}
        username: ${{ secrets.USERNAME }}
        key: ${{ secrets.KEY }}
        script: |
          cd banking-risk-platform
          ./deploy.sh
```

## 📞 Support and Troubleshooting

### Common Issues
1. **Port conflicts**: Check if ports are already in use
2. **Memory issues**: Increase EC2 instance size
3. **Database connection**: Verify PostgreSQL is running
4. **ML service errors**: Check model files exist

### Useful Commands
```bash
# Restart specific service
docker-compose -f docker-compose.prod.yml restart backend

# View service logs
docker-compose -f docker-compose.prod.yml logs backend

# Access database
docker exec -it banking-risk-platform_db_1 psql -U bank -d bankdb

# Update application
git pull && docker-compose -f docker-compose.prod.yml up -d --build
```

---

**Your Banking Risk Platform is now ready for live demonstration! 🎉**
