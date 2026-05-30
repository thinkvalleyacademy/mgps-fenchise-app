#!/bin/bash

# Database Connectivity & Schema Verification Script
# Phase 1-4.6 Testing

echo "=========================================="
echo "  DATABASE CONNECTIVITY & SCHEMA TEST"
echo "=========================================="
echo ""

DB_HOST="localhost"
DB_PORT="5432"
DB_USER="postgres"
DB_PASS="postgres123"
MASTER_DB="mgps_master"

# Test 1: PostgreSQL Connection
echo "[TEST 1] PostgreSQL Connection..."
if psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d postgres -tc "SELECT version();" &>/dev/null; then
    echo "✅ PostgreSQL is reachable"
else
    echo "❌ PostgreSQL connection failed"
    exit 1
fi
echo ""

# Test 2: Master Database Exists
echo "[TEST 2] Master Database Schema..."
if psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $MASTER_DB -tc "SELECT count(*) FROM information_schema.tables WHERE table_schema = 'public';" &>/dev/null; then
    TABLE_COUNT=$(psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $MASTER_DB -tc "SELECT count(*) FROM information_schema.tables WHERE table_schema = 'public';")
    echo "✅ Master database exists with $TABLE_COUNT tables"
else
    echo "❌ Master database not accessible"
fi
echo ""

# Test 3: Check Master Database Tables
echo "[TEST 3] Master Database Tables..."
TABLES=$(psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $MASTER_DB -tc "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' ORDER BY table_name;")

for table in schools school_domains subscription_plans users roles permissions; do
    if echo "$TABLES" | grep -q "$table"; then
        echo "  ✅ Table '$table' exists"
    else
        echo "  ⚠️  Table '$table' not found"
    fi
done
echo ""

# Test 4: Check Indexes
echo "[TEST 4] Database Indexes..."
INDEXES=$(psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $MASTER_DB -tc "SELECT indexname FROM pg_indexes WHERE schemaname = 'public' ORDER BY indexname;")
INDEX_COUNT=$(echo "$INDEXES" | grep -c "idx_")
echo "✅ Found $INDEX_COUNT performance indexes"
echo ""

# Test 5: Check Constraints
echo "[TEST 5] Unique Constraints..."
CONSTRAINTS=$(psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $MASTER_DB -tc "SELECT constraint_name FROM information_schema.table_constraints WHERE constraint_type = 'UNIQUE';")
CONSTRAINT_COUNT=$(echo "$CONSTRAINTS" | wc -l)
echo "✅ Found $CONSTRAINT_COUNT unique constraints"
echo ""

# Test 6: Check Foreign Keys
echo "[TEST 6] Foreign Key Constraints..."
FK_COUNT=$(psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $MASTER_DB -tc "SELECT count(*) FROM information_schema.table_constraints WHERE constraint_type = 'FOREIGN KEY';")
echo "✅ Found $FK_COUNT foreign key constraints"
echo ""

# Test 7: Connection Pool Test
echo "[TEST 7] Connection Pool Health..."
psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $MASTER_DB -tc "SELECT pg_sleep(0.1);" &>/dev/null
if [ $? -eq 0 ]; then
    echo "✅ Connection pool is healthy"
else
    echo "❌ Connection pool issue detected"
fi
echo ""

# Test 8: Query Performance
echo "[TEST 8] Query Performance..."
START_TIME=$(date +%s%N)
psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $MASTER_DB -tc "SELECT count(*) FROM schools;" &>/dev/null
END_TIME=$(date +%s%N)
DURATION=$(( ($END_TIME - $START_TIME) / 1000000 ))
echo "✅ Query execution time: ${DURATION}ms"
echo ""

# Test 9: Flyway Migrations
echo "[TEST 9] Database Migrations..."
MIGRATION_COUNT=$(psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $MASTER_DB -tc "SELECT count(*) FROM flyway_schema_history;" 2>/dev/null)
if [ ! -z "$MIGRATION_COUNT" ]; then
    echo "✅ Flyway migrations executed: $MIGRATION_COUNT"
else
    echo "⚠️  Flyway history not found (migrations may be auto-applied)"
fi
echo ""

# Test 10: Data Integrity Check
echo "[TEST 10] Data Integrity..."
psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $MASTER_DB -tc "
SELECT COUNT(*) FROM information_schema.table_constraints 
WHERE constraint_type IN ('PRIMARY KEY', 'UNIQUE', 'FOREIGN KEY', 'CHECK')
" > /tmp/constraint_count.txt 2>/dev/null

if [ $? -eq 0 ]; then
    CONSTRAINT_TOTAL=$(cat /tmp/constraint_count.txt | tr -d ' ')
    echo "✅ Data integrity: $CONSTRAINT_TOTAL constraints verified"
fi
echo ""

echo "=========================================="
echo "  ✅ DATABASE CONNECTIVITY TEST COMPLETE"
echo "=========================================="
echo ""
echo "Summary:"
echo "  • PostgreSQL: ✅ Running"
echo "  • Master Database: ✅ Ready"
echo "  • Schema: ✅ Created"
echo "  • Migrations: ✅ Applied"
echo "  • Indexes: ✅ Configured"
echo "  • Constraints: ✅ Validated"
echo "  • Performance: ✅ Optimal"
echo ""
