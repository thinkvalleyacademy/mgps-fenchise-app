# Deployment Guide

## Docker Setup
The system is containerized for easy deployment and local development.

### Services
- **postgres**: PostgreSQL database.
- **pgadmin**: Database management interface.
- **mgps-backend**: Spring Boot application.
- **mgps-frontend**: React application (Nginx served).

## Local Development
```bash
# Start infrastructure
docker-compose -f docker-files/postgres/docker-compose.yml up -d

# Run backend
cd mgpsfren-backend
./mvnw spring-boot:run

# Run frontend
cd mgpsfren-frontend
npm run dev
```

## Production Deployment
(To be detailed as the project matures)
- Recommended: AWS ECS / Kubernetes.
- CI/CD: GitHub Actions.
- Monitoring: Prometheus + Grafana.
- Logs: ELK Stack or CloudWatch.
