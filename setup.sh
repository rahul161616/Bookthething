#!/bin/bash

# Bookthething Setup Script
# This script helps set up the development environment

set -e

echo "🚀 Setting up Bookthething Microservices..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Check if Java 21 is installed
check_java() {
    print_info "Checking Java installation..."
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | grep -oP '(?<=version ").*?(?=")' | cut -d'.' -f1)
        if [[ "$JAVA_VERSION" -ge 21 ]]; then
            print_success "Java $JAVA_VERSION is installed"
        else
            print_error "Java 21 or higher is required. Current version: $JAVA_VERSION"
            exit 1
        fi
    else
        print_error "Java is not installed. Please install Java 21 or higher."
        exit 1
    fi
}

# Check if PostgreSQL is available
check_postgresql() {
    print_info "Checking PostgreSQL installation..."
    if command -v psql &> /dev/null; then
        print_success "PostgreSQL is installed"
    else
        print_warning "PostgreSQL is not installed. You'll need it to run the services."
        echo "Install with: sudo apt-get install postgresql postgresql-contrib (Ubuntu/Debian)"
        echo "Or: brew install postgresql (macOS)"
    fi
}

# Check if Docker is available
check_docker() {
    print_info "Checking Docker installation..."
    if command -v docker &> /dev/null; then
        print_success "Docker is installed"
        if command -v docker-compose &> /dev/null; then
            print_success "Docker Compose is installed"
        else
            print_warning "Docker Compose is not installed"
        fi
    else
        print_warning "Docker is not installed. Install it to use containerized deployment."
    fi
}

# Copy environment template
setup_env() {
    print_info "Setting up environment configuration..."
    if [[ ! -f .env ]]; then
        cp .env.example .env
        print_success "Created .env file from template"
        print_warning "Please update .env file with your actual configuration values"
    else
        print_info ".env file already exists"
    fi
}

# Setup database
setup_database() {
    print_info "Setting up PostgreSQL database..."
    read -p "Do you want to create the database now? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        echo "Creating database..."
        sudo -u postgres createdb bookingdb 2>/dev/null || print_warning "Database might already exist"
        sudo -u postgres psql -c "CREATE USER jugger WITH PASSWORD 'passw0rd';" 2>/dev/null || print_info "User might already exist"
        sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE bookingdb TO jugger;" 2>/dev/null
        print_success "Database setup completed"
    else
        print_info "Skipping database creation"
    fi
}

# Build services
build_services() {
    print_info "Building all services..."
    
    # Build Auth Service
    print_info "Building Auth Service..."
    cd auth-service
    ./mvnw clean install -DskipTests
    cd ..
    print_success "Auth Service built successfully"
    
    # Build User Service
    print_info "Building User Service..."
    cd userservice
    ./mvnw clean install -DskipTests
    cd ..
    print_success "User Service built successfully"
    
    # Build Metadata Service
    print_info "Building Metadata Service..."
    cd metadata-service
    ./mvnw clean install -DskipTests
    cd ..
    print_success "Metadata Service built successfully"
    
    # Build Vendor Service
    print_info "Building Vendor Service..."
    if [ -d "vendor-service" ] && [ -f "vendor-service/pom.xml" ]; then
        cd vendor-service
        if [ -f "./mvnw" ]; then
            ./mvnw clean install -DskipTests
        else
            mvn clean install -DskipTests
        fi
        cd ..
        print_success "Vendor Service built successfully"
    else
        print_warning "Vendor Service not found or incomplete, skipping..."
    fi
    
    # Build Futsal Service
    print_info "Building Futsal Service..."
    cd futsal-service
    ./mvnw clean install -DskipTests
    cd ..
    print_success "Futsal Service built successfully"
    
    # Build Booking Orchestrator
    print_info "Building Booking Orchestrator..."
    cd booking-orchestrator
    ./mvnw clean install -DskipTests
    cd ..
    print_success "Booking Orchestrator built successfully"
    
    # Build API Gateway
    print_info "Building API Gateway..."
    cd api-gateway
    ./mvnw clean install -DskipTests
    cd ..
    print_success "API Gateway built successfully"
    
    print_success "All services built successfully!"
}

# Run tests
run_tests() {
    print_info "Running tests..."
    
    cd auth-service
    ./mvnw test
    cd ..
    
    cd userservice
    ./mvnw test
    cd ..
    
    cd metadata-service
    ./mvnw test
    cd ..
    
    cd vendor-service
    ./mvnw test
    cd ..
    
    cd futsal-service
    ./mvnw test
    cd ..
    
    cd booking-orchestrator
    ./mvnw test
    cd ..
    
    cd api-gateway
    ./mvnw test
    cd ..
    
    print_success "All tests passed!"
}

# Create start script
create_start_script() {
    print_info "Creating start script..."
    cat > start-services.sh << 'EOF'
#!/bin/bash

# Start all services in correct order

echo "🚀 Starting Bookthething Microservices..."

# Start database first (if using Docker)
echo "Starting PostgreSQL database..."
docker-compose up -d postgres
sleep 10

# Start Auth Service (Port 8081)
echo "Starting Auth Service on port 8081..."
cd auth-service
nohup ./mvnw spring-boot:run > ../auth-service.log 2>&1 &
AUTH_PID=$!
cd ..
echo $AUTH_PID > auth-service.pid
sleep 15

# Start User Service (Port 8084)
echo "Starting User Service on port 8084..."
cd userservice
nohup ./mvnw spring-boot:run > ../userservice.log 2>&1 &
USER_PID=$!
cd ..
echo $USER_PID > userservice.pid
sleep 15

# Start Metadata Service (Port 8086)
echo "Starting Metadata Service on port 8086..."
cd metadata-service
nohup ./mvnw spring-boot:run > ../metadata-service.log 2>&1 &
METADATA_PID=$!
cd ..
echo $METADATA_PID > metadata-service.pid
sleep 15

# Start Vendor Service (Port 8083)
echo "Starting Vendor Service on port 8083..."
cd vendor-service
nohup ./mvnw spring-boot:run > ../vendor-service.log 2>&1 &
VENDOR_PID=$!
cd ..
echo $VENDOR_PID > vendor-service.pid
sleep 15

# Start Futsal Service (Port 8087)
echo "Starting Futsal Service on port 8087..."
cd futsal-service
nohup ./mvnw spring-boot:run > ../futsal-service.log 2>&1 &
FUTSAL_PID=$!
cd ..
echo $FUTSAL_PID > futsal-service.pid
sleep 15

# Start Booking Orchestrator (Port 8085)
echo "Starting Booking Orchestrator on port 8085..."
cd booking-orchestrator
nohup ./mvnw spring-boot:run > ../booking-orchestrator.log 2>&1 &
ORCHESTRATOR_PID=$!
cd ..
echo $ORCHESTRATOR_PID > orchestrator.pid
sleep 15

# Start API Gateway last (Port 8080)
echo "Starting API Gateway on port 8080..."
cd api-gateway
nohup ./mvnw spring-boot:run > ../api-gateway.log 2>&1 &
GATEWAY_PID=$!
cd ..
echo $GATEWAY_PID > api-gateway.pid
sleep 10

echo "✅ All services started!"
echo ""
echo "Service URLs:"
echo "- API Gateway: http://localhost:8080"
echo "- Auth Service: http://localhost:8081"
echo "- Vendor Service: http://localhost:8083"
echo "- User Service: http://localhost:8084"
echo "- Booking Orchestrator: http://localhost:8085"
echo "- Metadata Service: http://localhost:8086"
echo "- Futsal Service: http://localhost:8087"
echo ""
echo "Service PIDs saved to .pid files"
echo "Logs available in respective .log files"
echo ""
echo "To stop services, run: ./stop-services.sh"
EOF

    chmod +x start-services.sh
    print_success "Created start-services.sh"
}

# Create stop script
create_stop_script() {
    cat > stop-services.sh << 'EOF'
#!/bin/bash

# Stop all services

echo "🛑 Stopping Bookthething Microservices..."

# Function to stop service by pid file
stop_service() {
    local service_name=$1
    local pid_file=$2
    
    if [[ -f $pid_file ]]; then
        PID=$(cat $pid_file)
        if kill $PID 2>/dev/null; then
            echo "✅ $service_name stopped (PID: $PID)"
        else
            echo "⚠️  $service_name was not running or already stopped"
        fi
        rm $pid_file
    else
        echo "⚠️  No PID file found for $service_name"
    fi
}

# Stop services in reverse order
stop_service "API Gateway" "api-gateway.pid"
stop_service "Booking Orchestrator" "orchestrator.pid" 
stop_service "Futsal Service" "futsal-service.pid"
stop_service "Vendor Service" "vendor-service.pid"
stop_service "Metadata Service" "metadata-service.pid"
stop_service "User Service" "userservice.pid"
stop_service "Auth Service" "auth-service.pid"

# Stop database
echo "Stopping PostgreSQL database..."
docker-compose down postgres 2>/dev/null || echo "Database already stopped"

# Clean up any remaining processes
pkill -f "spring-boot:run" 2>/dev/null || true

echo "✅ All services stopped!"

# Clean up log files if desired
read -p "Do you want to clean up log files? (y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    rm -f *.log
    echo "✅ Log files cleaned up!"
fi
EOF

    chmod +x stop-services.sh
    print_success "Created stop-services.sh"
}

# Main setup flow
main() {
    echo "========================================"
    echo "  Bookthething Microservices Setup"
    echo "========================================"
    echo ""
    
    check_java
    check_postgresql
    check_docker
    
    echo ""
    setup_env
    
    echo ""
    read -p "Do you want to set up the database now? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        setup_database
    fi
    
    echo ""
    read -p "Do you want to build all services now? (Y/n): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Nn]$ ]]; then
        build_services
    fi
    
    echo ""
    read -p "Do you want to run tests? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        run_tests
    fi
    
    echo ""
    create_start_script
    create_stop_script
    
    echo ""
    echo "========================================"
    print_success "Setup completed!"
    echo "========================================"
    echo ""
    echo "Next steps:"
    echo "1. Update .env file with your configuration"
    echo "2. Run: ./start-services.sh to start all services"
    echo "3. Access API Gateway at: http://localhost:8080"
    echo "4. Check service health at respective ports:"
    echo "   - Auth Service: http://localhost:8081/actuator/health"
    echo "   - Vendor Service: http://localhost:8083/actuator/health"
    echo "   - User Service: http://localhost:8084/actuator/health"
    echo "   - Booking Orchestrator: http://localhost:8085/actuator/health"
    echo "   - Metadata Service: http://localhost:8086/actuator/health"
    echo "   - Futsal Service: http://localhost:8087/actuator/health"
    echo ""
    echo "For Docker deployment:"
    echo "- Run: docker-compose up -d"
    echo ""
    echo "Happy coding! 🎉"
}

# Run main function
main "$@"