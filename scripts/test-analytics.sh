#!/bin/bash

# Analytics Testing Script for Banking Risk Platform
# This script tests the analytics endpoints and Snowflake integration

echo "🧪 Testing Banking Risk Platform Analytics"
echo "=========================================="

BASE_URL="http://localhost:8080"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to test endpoint
test_endpoint() {
    local endpoint=$1
    local description=$2
    
    echo -e "\n${YELLOW}Testing: $description${NC}"
    echo "Endpoint: $BASE_URL$endpoint"
    
    response=$(curl -s -w "\n%{http_code}" "$BASE_URL$endpoint")
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n -1)
    
    if [ "$http_code" -eq 200 ]; then
        echo -e "${GREEN}✅ Success (HTTP $http_code)${NC}"
        echo "Response preview:"
        echo "$body" | head -c 200
        if [ ${#body} -gt 200 ]; then
            echo "..."
        fi
    else
        echo -e "${RED}❌ Failed (HTTP $http_code)${NC}"
        echo "Response: $body"
    fi
}

# Test basic health
test_endpoint "/health" "Backend Health Check"

# Test analytics endpoints
test_endpoint "/analytics/health" "Snowflake Analytics Health Check"
test_endpoint "/analytics/summary" "Analytics Summary"
test_endpoint "/analytics/risk-distribution" "Risk Score Distribution"
test_endpoint "/analytics/approval-rates" "Approval Rates Over Time"
test_endpoint "/analytics/top-factors" "Top Global Risk Factors"

# Test loan data
test_endpoint "/loans/recent" "Recent Loan Applications"

echo -e "\n${GREEN}🎉 Analytics Testing Complete!${NC}"
echo ""
echo "Next Steps:"
echo "1. Run the Snowflake views script: docs/snowflake-analytics-views.sql"
echo "2. Connect Tableau using the guide: docs/TABLEAU_INTEGRATION.md"
echo "3. Create dashboards for risk score distribution, approval rates, and top factors"
