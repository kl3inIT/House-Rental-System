# RentEase - House Rental Application

A Spring Boot application for managing house rentals with features like property listing, user authentication, and booking management.

## Prerequisites

Before running this project, make sure you have the following installed:

- Docker and Docker Compose
- Java 21 or higher (for local development)
- Maven 3.9 or higher (for local development)
- Node.js 16 or higher
- npm 8 or higher

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/yourusername/house-rental.git
cd house-rental
```

### 2. Environment Setup

1. Create a `.env` file in the root directory with the following variables:
```env
SPRING_DATASOURCE_URL=jdbc:sqlserver://localhost:1434;databaseName=HouseRentalDB;encrypt=true;trustServerCertificate=true
SPRING_DATASOURCE_USERNAME=sa
SPRING_DATASOURCE_PASSWORD=Dat12345!
SPRING_MAIL_USERNAME=your-email@gmail.com
SPRING_MAIL_PASSWORD=your-app-password
```

### 3. Running with Docker (Recommended)

1. Build and start the containers:
```bash
docker-compose up -d
```

This will start:
- Spring Boot application on port 8080
- SQL Server on port 1434
- Redis on port 6380

2. Access the application:
- Web application: http://localhost:8080/house-rental
- SQL Server: localhost:1434
- Redis: localhost:6380

### 4. Local Development Setup

1. Install Node.js dependencies:
```bash
npm install
```

2. Build CSS:
```bash
# For development
npm run dev

# For production
npm run build
```

3. Run the application:
```bash
mvn spring-boot:run
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/rental/houserental/
│   │       ├── controller/
│   │       ├── service/
│   │       ├── repository/
│   │       ├── model/
│   │       └── config/
│   └── resources/
│       ├── static/
│       │   ├── css/
│       │   └── js/
│       └── templates/
└── test/
```

## Technology Stack

### Backend
- Spring Boot 3.x
- Spring Security
- Spring Data JPA
- SQL Server
- Redis for caching
- Thymeleaf
- Java Mail

### Frontend
- Tailwind CSS
- JavaScript
- Font Awesome icons

### Infrastructure
- Docker & Docker Compose
- SQL Server 2022
- Redis 7

## Features

- User Authentication (Login/Register)
- Property Listing
- Search and Filter Properties
- Booking Management
- User Profile Management
- Admin Dashboard
- Email Verification
- Password Reset
- Remember Me functionality
- Session Management

## Development

### Frontend Development

The project uses Tailwind CSS for styling. To modify styles:

1. Edit files in `src/main/resources/static/css/main.css`
2. Run `npm run dev` to watch for changes
3. For production, run `npm run build`

### Backend Development

- Spring Boot 3.x
- Spring Security
- Spring Data JPA
- Thymeleaf
- SQL Server

### Docker Commands

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down

# Rebuild and start
docker-compose up -d --build

# Remove volumes
docker-compose down -v
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact

Your Name - your.email@example.com

Project Link: [https://github.com/yourusername/house-rental](https://github.com/yourusername/house-rental) 