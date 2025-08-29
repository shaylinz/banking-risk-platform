# Snowflake Integration Setup

This document explains how to set up Snowflake integration for the Banking Risk Platform.

## Prerequisites

1. A Snowflake account with appropriate permissions
2. Access to create databases, schemas, and tables
3. A warehouse for compute resources

## Setup Steps

### 1. Create Snowflake Objects

Run the SQL script `docs/snowflake-schema.sql` in your Snowflake console to create:
- Database: `BKRISK_DB`
- Schema: `CORE`
- Table: `LOAN_APPLICATIONS`
- View: `LOAN_APPLICATIONS_ANALYTICS`

### 2. Configure Environment Variables

Set the following environment variables in your `docker-compose.yml` or as system environment variables:

```bash
# Snowflake Connection
SNOWFLAKE_URL=jdbc:snowflake://your-account.snowflakecomputing.com
SNOWFLAKE_USERNAME=your-username
SNOWFLAKE_PASSWORD=your-password
SNOWFLAKE_DATABASE=BKRISK_DB
SNOWFLAKE_SCHEMA=CORE
SNOWFLAKE_WAREHOUSE=COMPUTE_WH
```

### 3. Update docker-compose.yml (Optional)

Add the environment variables to the backend service in `docker-compose.yml`:

```yaml
backend:
  build:
    context: ./backend
  depends_on:
    db:
      condition: service_healthy
    ml:
      condition: service_healthy
  ports:
    - "8080:8080"
  environment:
    SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/bankdb
    SPRING_DATASOURCE_USERNAME: bank
    SPRING_DATASOURCE_PASSWORD: bankpass
    SPRING_JPA_HIBERNATE_DDL_AUTO: update
    ML_BASE_URL: http://ml:8000
    # Add Snowflake configuration
    SNOWFLAKE_URL: jdbc:snowflake://your-account.snowflakecomputing.com
    SNOWFLAKE_USERNAME: your-username
    SNOWFLAKE_PASSWORD: your-password
    SNOWFLAKE_DATABASE: BKRISK_DB
    SNOWFLAKE_SCHEMA: CORE
    SNOWFLAKE_WAREHOUSE: COMPUTE_WH
```

### 4. Test the Integration

1. Start the services: `docker-compose up -d`
2. Submit a loan application through the frontend or API
3. Check Snowflake to verify the data was inserted:

```sql
SELECT * FROM BKRISK_DB.CORE.LOAN_APPLICATIONS ORDER BY CREATED_AT DESC LIMIT 5;
```

## Data Flow

1. **Frontend** submits loan application
2. **Backend** receives request and calls ML service
3. **ML Service** returns risk score and decision
4. **Backend** saves to **Postgres** (system of record)
5. **Backend** writes to **Snowflake** (analytics store)
6. **Tableau** can connect to Snowflake for dashboards

## Troubleshooting

### Connection Issues
- Verify Snowflake URL format: `jdbc:snowflake://account.snowflakecomputing.com`
- Check username/password credentials
- Ensure warehouse is running and accessible

### Permission Issues
- Verify the user has INSERT permissions on `BKRISK_DB.CORE.LOAN_APPLICATIONS`
- Check that the role has access to the warehouse

### Data Issues
- Check backend logs for Snowflake write errors
- Verify the table schema matches the expected format
- Ensure all required fields are being populated

## Analytics View

The `LOAN_APPLICATIONS_ANALYTICS` view provides:
- All loan application data
- Derived fields for analysis (IS_APPROVED, RISK_CATEGORY)
- Date-based aggregations
- Ready for Tableau integration

## Security Notes

- Store Snowflake credentials securely (use environment variables or secrets management)
- Consider using Snowflake's key pair authentication for production
- Implement proper role-based access control in Snowflake
- Monitor warehouse usage and costs
