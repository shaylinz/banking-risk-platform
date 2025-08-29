# Tableau Integration Guide

This guide explains how to connect Tableau to the Banking Risk Platform's Snowflake data warehouse for analytics and reporting.

## Prerequisites

1. **Tableau Desktop** or **Tableau Server** installed
2. **Snowflake account** with access to the BKRISK_DB database
3. **Snowflake credentials** (username, password, account URL)
4. **Snowflake views** created (run `docs/snowflake-analytics-views.sql`)

## Snowflake Connection Setup

### 1. Create Snowflake Views

First, run the analytics views script in your Snowflake console:

```sql
-- Run this in Snowflake
-- File: docs/snowflake-analytics-views.sql
```

This creates the following views:
- `V_LOAN_APPLICATIONS` - Main loan data with derived fields
- `V_FACTORS` - SHAP values and feature analysis
- `V_DAILY_APPROVAL_RATES` - Daily approval rate trends
- `V_RISK_SCORE_DISTRIBUTION` - Risk score distribution analysis
- `V_GLOBAL_RISK_FACTORS` - Top global risk factors

### 2. Connection Details

Use these connection parameters in Tableau:

```
Server: uinmrec-gp26051.snowflakecomputing.com
Database: BKRISK_DB
Schema: CORE
Warehouse: BKRISK_WH
Username: SHAYLINZ
Password: [your-password]
Role: PUBLIC
```

## Tableau Connection Steps

### Step 1: Connect to Snowflake

1. Open **Tableau Desktop**
2. Click **"Connect to Data"**
3. Select **"Snowflake"** from the connectors list
4. Enter connection details:
   - **Server**: `uinmrec-gp26051.snowflakecomputing.com`
   - **Database**: `BKRISK_DB`
   - **Schema**: `CORE`
   - **Warehouse**: `BKRISK_WH`
   - **Username**: `SHAYLINZ`
   - **Password**: Your Snowflake password

### Step 2: Select Data Source

Choose one of the following views as your primary data source:

- **V_LOAN_APPLICATIONS** - For comprehensive loan analysis
- **V_DAILY_APPROVAL_RATES** - For approval rate trends
- **V_RISK_SCORE_DISTRIBUTION** - For risk score analysis
- **V_GLOBAL_RISK_FACTORS** - For factor importance analysis

### Step 3: Create Relationships (Optional)

For complex dashboards, you can create relationships between views:

1. **Data** → **New Data Source**
2. Add additional views
3. **Data** → **Edit Relationships**
4. Create relationships based on common fields (e.g., APPLICATION_ID)

## Dashboard Examples

### 1. Risk Score Distribution Dashboard

**Data Source**: `V_RISK_SCORE_DISTRIBUTION`

**Visualizations**:
- **Bar Chart**: Risk Score Bucket vs Application Count
- **Line Chart**: Risk Score Bucket vs Approval Rate %
- **Pie Chart**: Distribution of Risk Categories

**Key Metrics**:
- Total applications by risk bucket
- Approval rate by risk score range
- Risk score distribution patterns

### 2. Approval Rates Over Time Dashboard

**Data Source**: `V_DAILY_APPROVAL_RATES`

**Visualizations**:
- **Line Chart**: Application Date vs Approval Rate %
- **Bar Chart**: Application Date vs Total Applications
- **Dual Axis**: Approval Rate % and Average Risk Score over time

**Key Metrics**:
- Daily approval rate trends
- Application volume patterns
- Risk score correlation with approval rates

### 3. Top Global Risk Factors Dashboard

**Data Source**: `V_GLOBAL_RISK_FACTORS`

**Visualizations**:
- **Horizontal Bar Chart**: Factor Name vs Average Impact
- **Bubble Chart**: Factor Name vs Average Impact vs Application Count
- **Table**: Detailed factor statistics

**Key Metrics**:
- Most influential risk factors
- Factor impact distribution
- Factor coverage across applications

### 4. Comprehensive Loan Analytics Dashboard

**Data Source**: `V_LOAN_APPLICATIONS`

**Visualizations**:
- **Scatter Plot**: Age vs Risk Score (colored by Decision)
- **Histogram**: Monthly Income Distribution
- **Heat Map**: Age Group vs Income Group vs Approval Rate
- **Box Plot**: Risk Score by Decision

**Key Metrics**:
- Demographic analysis
- Income vs approval patterns
- Risk score patterns by decision

## Sample Queries for Testing

### Test Analytics Endpoints

The backend provides analytics endpoints for testing:

```bash
# Health check
curl http://localhost:8080/analytics/health

# Risk distribution
curl http://localhost:8080/analytics/risk-distribution

# Approval rates
curl http://localhost:8080/analytics/approval-rates

# Top factors
curl http://localhost:8080/analytics/top-factors

# Summary statistics
curl http://localhost:8080/analytics/summary
```

### Direct Snowflake Queries

Test these queries directly in Snowflake:

```sql
-- Check data availability
SELECT COUNT(*) FROM V_LOAN_APPLICATIONS;

-- Risk score distribution
SELECT * FROM V_RISK_SCORE_DISTRIBUTION;

-- Recent approval rates
SELECT * FROM V_DAILY_APPROVAL_RATES 
WHERE APPLICATION_DATE >= DATEADD(day, -30, CURRENT_DATE());

-- Top risk factors
SELECT * FROM V_GLOBAL_RISK_FACTORS;
```

## Dashboard Best Practices

### 1. Performance Optimization

- **Use extracts** for large datasets
- **Filter data** at the source when possible
- **Limit date ranges** for time-series analysis
- **Use aggregations** instead of detailed records

### 2. Visualization Guidelines

- **Risk scores**: Use color gradients (green=low risk, red=high risk)
- **Approval rates**: Use percentage formatting
- **Time series**: Use consistent date formatting
- **Factors**: Use horizontal bar charts for readability

### 3. Interactivity

- **Add filters** for date ranges, risk categories, age groups
- **Create parameters** for threshold values
- **Use actions** to drill down from summary to detail
- **Add tooltips** with detailed information

## Troubleshooting

### Common Issues

1. **Connection Failed**
   - Verify Snowflake credentials
   - Check warehouse is running
   - Ensure user has proper permissions

2. **No Data Visible**
   - Verify views exist in Snowflake
   - Check data exists in base tables
   - Run sample queries in Snowflake console

3. **Performance Issues**
   - Use smaller date ranges
   - Create extracts for large datasets
   - Optimize warehouse size

4. **Missing Views**
   - Run `docs/snowflake-analytics-views.sql`
   - Check user permissions on views
   - Verify schema and database names

### Support

For issues with:
- **Snowflake connection**: Check Snowflake console and logs
- **Tableau setup**: Refer to Tableau documentation
- **Data issues**: Check backend logs and Snowflake queries
- **Performance**: Monitor warehouse usage and query performance

## Security Considerations

1. **Credential Management**
   - Store credentials securely
   - Use role-based access control
   - Rotate passwords regularly

2. **Data Access**
   - Limit user permissions to necessary views
   - Audit data access regularly
   - Consider data masking for sensitive fields

3. **Network Security**
   - Use secure connections (HTTPS)
   - Restrict access to authorized IPs
   - Monitor connection logs

## Next Steps

1. **Create initial dashboards** using the provided examples
2. **Customize visualizations** for your specific needs
3. **Set up automated refreshes** for real-time data
4. **Share dashboards** with stakeholders
5. **Monitor usage** and optimize performance
