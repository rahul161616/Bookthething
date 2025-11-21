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
    
    # Build API Gateway
    print_info "Building API Gateway..."
    cd api-gateway
    ./mvnw clean install -DskipTests
    cd ..
    print_success "API Gateway built successfully"
    
    # Build Booking Service
    print_info "Building Booking Service..."
    cd booking-service
    ./mvnw clean install -DskipTests
    cd ..
    print_success "Booking Service built successfully"
    
    print_success "All services built successfully!"
}

# Run tests
run_tests() {
    print_info "Running tests..."
    
    cd api-gateway
    ./mvnw test
    cd ..
    
    cd booking-service
    ./mvnw test
    cd ..
    
    print_success "All tests passed!"
}

# Create start script
create_start_script() {
    print_info "Creating start script..."
    cat > start-services.sh << 'EOF'
#!/bin/bash

# Start all services

echo "🚀 Starting Bookthething Services..."

# Start API Gateway
echo "Starting API Gateway on port 8080..."
cd api-gateway
./mvnw spring-boot:run &
API_GATEWAY_PID=$!
cd ..

# Wait a moment
sleep 5

# Start Booking Service
echo "Starting Booking Service on port 8082..."
cd booking-service
./mvnw spring-boot:run &
BOOKING_SERVICE_PID=$!
cd ..

echo "Services started!"
echo "API Gateway PID: $API_GATEWAY_PID"
echo "Booking Service PID: $BOOKING_SERVICE_PID"

echo ""
echo "Service URLs:"
echo "- API Gateway: http://localhost:8080"
echo "- Booking Service: http://localhost:8082"
echo ""
echo "To stop services, run: ./stop-services.sh"

# Save PIDs for stop script
echo "$API_GATEWAY_PID" > .api-gateway.pid
echo "$BOOKING_SERVICE_PID" > .booking-service.pid
EOF

    chmod +x start-services.sh
    print_success "Created start-services.sh"
}

# Create stop script
create_stop_script() {
    cat > stop-services.sh << 'EOF'
#!/bin/bash

# Stop all services

echo "🛑 Stopping Bookthething Services..."

# Stop API Gateway
if [[ -f .api-gateway.pid ]]; then
    API_GATEWAY_PID=$(cat .api-gateway.pid)
    kill $API_GATEWAY_PID 2>/dev/null || echo "API Gateway already stopped"
    rm .api-gateway.pid
fi

# Stop Booking Service
if [[ -f .booking-service.pid ]]; then
    BOOKING_SERVICE_PID=$(cat .booking-service.pid)
    kill $BOOKING_SERVICE_PID 2>/dev/null || echo "Booking Service already stopped"
    rm .booking-service.pid
fi

echo "Services stopped!"
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
    echo ""
    echo "For Docker deployment:"
    echo "- Run: docker-compose up -d"
    echo ""
    echo "Happy coding! 🎉"
}

# Run main function
main "$@"