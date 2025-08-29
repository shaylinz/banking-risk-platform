# 🏦 Banking Risk Platform

A full-stack demo banking application that demonstrates AI/ML-powered credit risk scoring with explainable AI (SHAP), real-time analytics, and modern cloud architecture.

## 🎯 Project Overview

This platform showcases a production-ready banking risk assessment system with:

- **🤖 AI/ML Integration**: XGBoost model with SHAP explainability
- **⚡ Real-time Processing**: Instant loan decisions with risk scores
- **📊 Analytics Pipeline**: Postgres → Snowflake → Tableau
- **🐳 Containerized**: Fully Dockerized for easy deployment
- **🌐 Modern Stack**: React + Spring Boot + FastAPI

## 🏗️ Architecture

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   React     │───▶│ Spring Boot │───▶│   FastAPI   │───▶│  PostgreSQL │
│  Frontend   │    │   Backend   │    │   ML Service│    │   (Primary) │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
                                                              │
                                                              ▼
                                                   ┌─────────────┐
                                                   │  Snowflake  │
                                                   │ (Analytics) │
                                                   └─────────────┘
                                                              │
                                                              ▼
                                                   ┌─────────────┐
                                                   │   Tableau   │
                                                   │ (Dashboards)│
                                                   └─────────────┘
```

## ✨ Features

### 🎯 Core Functionality
- **Loan Application Form**: 10 credit risk features
- **Instant Risk Scoring**: Real-time ML predictions
- **Explainable AI**: SHAP factor analysis
- **Decision Dashboard**: Recent applications and scores

### 📊 Analytics & BI
- **Dual Data Storage**: Postgres (operational) + Snowflake (analytics)
- **Real-time Streaming**: Automatic data sync to Snowflake
- **Tableau Integration**: Pre-built analytics views
- **Risk Score Distribution**: Visual analytics dashboards

### 🔧 Technical Features
- **Docker Compose**: One-command deployment
- **Health Checks**: Automated service monitoring
- **Error Handling**: Graceful failure management
- **CORS Configuration**: Cross-origin request support

## 🚀 Quick Start

### Prerequisites
- **Docker & Docker Compose**
- **Snowflake Account** (for analytics)
- **Tableau Desktop** (optional, for dashboards)

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/banking-risk-platform.git
cd banking-risk-platform
```

### 2. Configure Snowflake (Optional)
If you want analytics features:

1. **Update credentials** in `docker-compose.yml`:
```yaml
environment:
  SNOWFLAKE_URL: jdbc:snowflake://your-account.snowflakecomputing.com
  SNOWFLAKE_USERNAME: your-username
  SNOWFLAKE_PASSWORD: your-password
  SNOWFLAKE_DATABASE: BKRISK_DB
  SNOWFLAKE_SCHEMA: CORE
  SNOWFLAKE_WAREHOUSE: COMPUTE_WH
```

2. **Run Snowflake setup** in your Snowflake console:
```sql
-- Execute: docs/snowflake-schema.sql
-- Execute: docs/snowflake-analytics-views.sql
```

### 3. Start the Platform
```bash
# Start all services
docker-compose up -d

# Check service status
docker-compose ps
```

### 4. Access the Application
- **Frontend**: http://localhost:5173
- **Backend API**: http://localhost:8080
- **ML Service**: http://localhost:8000
- **PostgreSQL**: localhost:5433

### 5. Test the Platform
```bash
# Test loan application
curl -X POST http://localhost:8080/loans/apply \
  -H "Content-Type: application/json" \
  -d '{
    "RevolvingUtilizationOfUnsecuredLines": 0.45,
    "age": 35,
    "NumberOfTime30_59DaysPastDueNotWorse": 0,
    "DebtRatio": 0.25,
    "MonthlyIncome": 5000,
    "NumberOfOpenCreditLinesAndLoans": 6,
    "NumberOfTimes90DaysLate": 0,
    "NumberRealEstateLoansOrLines": 1,
    "NumberOfTime60_89DaysPastDueNotWorse": 0,
    "NumberOfDependents": 1
  }'

# Test analytics endpoints
curl http://localhost:8080/analytics/health
curl http://localhost:8080/analytics/summary
```

## 📁 Project Structure

```
banking-risk-platform/
├── frontend/                 # React application
│   ├── src/pages/           # React components
│   ├── package.json         # Frontend dependencies
│   └── vite.config.js       # Vite configuration
├── backend/                  # Spring Boot application
│   ├── src/main/java/       # Java source code
│   ├── pom.xml              # Maven dependencies
│   └── Dockerfile           # Backend container
├── ml-service/              # FastAPI ML service
│   ├── service/app.py       # ML API endpoints
│   ├── artifacts/           # Trained models
│   ├── requirements.txt     # Python dependencies
│   └── Dockerfile           # ML service container
├── docs/                    # Documentation
│   ├── snowflake-schema.sql # Database setup
│   ├── snowflake-analytics-views.sql # Analytics views
│   └── TABLEAU_INTEGRATION.md # Tableau guide
├── scripts/                 # Utility scripts
├── docker-compose.yml       # Service orchestration
└── README.md               # This file
```

## 🔧 Configuration

### Environment Variables
Key configuration options in `docker-compose.yml`:

```yaml
# Database
SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/bankdb
SPRING_DATASOURCE_USERNAME: bank
SPRING_DATASOURCE_PASSWORD: bankpass

# ML Service
ML_BASE_URL: http://ml:8000

# Snowflake (optional)
SNOWFLAKE_URL: jdbc:snowflake://your-account.snowflakecomputing.com
SNOWFLAKE_USERNAME: your-username
SNOWFLAKE_PASSWORD: your-password
```

### Ports
- **5173**: React frontend
- **8080**: Spring Boot backend
- **8000**: FastAPI ML service
- **5433**: PostgreSQL database

## 📊 Analytics Setup

### 1. Snowflake Views
Run these scripts in your Snowflake console:
- `docs/snowflake-schema.sql` - Creates base tables
- `docs/snowflake-analytics-views.sql` - Creates analytics views

### 2. Tableau Connection
Follow the guide in `docs/TABLEAU_INTEGRATION.md` to connect Tableau and create dashboards for:
- Risk score distribution
- Approval rates over time
- Top global risk factors

## 🧪 Testing

### API Endpoints
```bash
# Health checks
curl http://localhost:8080/health
curl http://localhost:8000/health

# Loan application
curl -X POST http://localhost:8080/loans/apply -H "Content-Type: application/json" -d '{...}'

# Analytics
curl http://localhost:8080/analytics/health
curl http://localhost:8080/analytics/summary
curl http://localhost:8080/analytics/risk-distribution
```

### Automated Testing
```bash
# Run the test script
chmod +x scripts/test-analytics.sh
./scripts/test-analytics.sh
```

## 🐛 Troubleshooting

### Common Issues

1. **Services not starting**
   ```bash
   docker-compose logs [service-name]
   docker-compose down && docker-compose up -d
   ```

2. **Frontend not loading**
   - Check if backend is running on port 8080
   - Verify CORS configuration
   - Check browser console for errors

3. **ML service errors**
   - Verify model files exist in `ml-service/artifacts/`
   - Check ML service logs: `docker-compose logs ml`

4. **Snowflake connection issues**
   - Verify credentials in `docker-compose.yml`
   - Check warehouse is running
   - Ensure proper permissions

### Logs
```bash
# View all logs
docker-compose logs

# View specific service logs
docker-compose logs backend
docker-compose logs ml
docker-compose logs frontend
```

## 🚀 Deployment

### Local Development
```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

### Production Deployment
1. **Update environment variables** for production
2. **Configure SSL certificates** for HTTPS
3. **Set up monitoring** and logging
4. **Configure backup strategies** for databases
5. **Deploy to cloud platform** (AWS, GCP, Azure)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **XGBoost** for the machine learning model
- **SHAP** for explainable AI
- **Spring Boot** for the backend framework
- **React** for the frontend
- **FastAPI** for the ML service
- **Snowflake** for data warehousing
- **Tableau** for analytics visualization

## 📞 Support

For questions or issues:
- Create an issue on GitHub
- Check the troubleshooting section
- Review the documentation in the `docs/` folder

---

**Built with ❤️ for demonstrating modern banking technology**