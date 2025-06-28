# RentEase - House Rental Platform

A modern Spring Boot application for managing house rentals with features like property listing, user authentication, booking management, and admin dashboard.

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![Tailwind CSS](https://img.shields.io/badge/Tailwind%20CSS-3.x-blue)
![SQL Server](https://img.shields.io/badge/SQL%20Server-2022-red)

## 🚀 Features

### For Users
- **Authentication**: Secure login/register with email verification
- **Property Search**: Advanced search and filter capabilities  
- **Property Details**: Detailed property views with image galleries
- **Booking Management**: Request and manage property bookings
- **Profile Management**: Update personal information and preferences
- **Favorites**: Save and manage favorite properties

### For Landlords
- **Property Management**: Add, edit, and manage property listings
- **Dashboard**: Overview of properties, bookings, and revenue
- **Image Upload**: Multiple property images with drag-and-drop interface
- **Booking Requests**: Review and respond to tenant requests
- **Analytics**: Property performance and viewing statistics

### For Admins
- **Admin Dashboard**: System overview and user management
- **User Management**: Manage user accounts and permissions
- **Property Oversight**: Monitor and moderate property listings
- **System Analytics**: Platform usage and performance metrics

## 📋 Prerequisites

- **Java 21+** (for local development)
- **Maven 3.9+** (for local development)
- **Node.js 16+** & **npm 8+** (for CSS/frontend build)
- **Docker & Docker Compose** (recommended for easy setup)

## 🛠️ Quick Start

### Option 1: Docker Setup (Recommended)

1. **Clone the repository**
```bash
git clone https://github.com/yourusername/house-rental.git
cd house-rental
```

2. **Environment Setup**
Create a `.env` file:
```env
SPRING_DATASOURCE_URL=jdbc:sqlserver://localhost:1434;databaseName=HouseRentalDB;encrypt=true;trustServerCertificate=true
SPRING_DATASOURCE_USERNAME=sa
SPRING_DATASOURCE_PASSWORD=Dat12345!
SPRING_MAIL_USERNAME=your-email@gmail.com
SPRING_MAIL_PASSWORD=your-app-password
```

3. **Start with Docker**
```bash
docker-compose up -d
```

4. **Access the application**
- 🌐 Web app: http://localhost:8080
- 🗄️ SQL Server: localhost:1434
- 📦 Redis: localhost:6380

### Option 2: Local Development

1. **Install dependencies**
```bash
npm install
```

2. **Build CSS (one-time)**
```bash
npm run build:css:prod
```

3. **Run the application**
```bash
./mvnw spring-boot:run
```

## 🎨 CSS Development Workflow

This project uses **Tailwind CSS** with a custom build system:

### File Structure
```
src/main/resources/static/css/
├── input.css      # 📝 Source file (edit this)
└── main.css       # 🔄 Built file (auto-generated)
```

### Development Commands

**For active development (watch mode):**
```bash
# Windows
./dev-css.bat

# Linux/Mac  
./dev-css.sh

# Or directly
npm run build:css
```

**For production build:**
```bash
npm run build:css:prod
```

**Auto-build on app start:**
```bash
mvn spring-boot:run  # CSS builds automatically
```

### Workflow
1. ✏️ Edit `src/main/resources/static/css/input.css`
2. 🔄 Run watch mode: `npm run build:css`
3. 💾 CSS compiles to `main.css` automatically
4. 🌐 Refresh browser to see changes

## ��️ Project Structure

```
📁 HouseRental/
├── 📁 src/main/
│   ├── 📁 java/com/rental/houserental/
│   │   ├── 📁 controller/          # REST controllers
│   │   │   ├── 📁 admin/          # Admin endpoints
│   │   │   ├── 📁 landlord/       # Landlord endpoints  
│   │   │   └── 📁 user/           # User endpoints
│   │   ├── 📁 service/            # Business logic
│   │   ├── 📁 repository/         # Data access
│   │   ├── 📁 entity/             # JPA entities
│   │   ├── 📁 dto/                # Data transfer objects
│   │   ├── 📁 security/           # Security configuration
│   │   ├── 📁 config/             # App configuration
│   │   └── 📁 exceptions/         # Custom exceptions
│   └── 📁 resources/
│       ├── 📁 static/
│       │   ├── 📁 css/            # Stylesheets
│       │   └── 📁 js/             # JavaScript files
│       └── 📁 templates/          # Thymeleaf templates
│           ├── 📁 admin/          # Admin pages
│           ├── 📁 landlord/       # Landlord pages
│           ├── 📁 user/           # User pages
│           ├── 📁 fragments/      # Reusable components
│           └── 📁 layout/         # Page layouts
├── 📄 package.json               # npm dependencies & scripts
├── 📄 tailwind.config.js         # Tailwind configuration
├── 📄 pom.xml                    # Maven dependencies
└── 📄 docker-compose.yml         # Docker setup
```

## 🔧 Technology Stack

### Backend
- **Spring Boot 3.x** - Application framework
- **Spring Security** - Authentication & authorization
- **Spring Data JPA** - Database operations
- **Thymeleaf** - Server-side templating
- **SQL Server** - Primary database
- **Redis** - Caching & session storage
- **Java Mail** - Email notifications

### Frontend
- **Tailwind CSS 3.x** - Utility-first CSS framework
- **JavaScript ES6+** - Client-side interactions
- **Font Awesome 6** - Icons
- **Inter Font** - Typography

### Infrastructure
- **Docker & Docker Compose** - Containerization
- **Maven** - Build tool & dependency management
- **npm** - Frontend package management

## 🔐 Security Features

- **JWT Authentication** with refresh tokens
- **Email Verification** for new accounts
- **Password Reset** with secure tokens
- **CSRF Protection** on all forms
- **Session Management** with Redis
- **Role-based Access Control** (User, Landlord, Admin)
- **Remember Me** functionality

## 🚀 Deployment

### Production Build
```bash
# Build CSS for production
npm run build:css:prod

# Create jar file
./mvnw clean package -DskipTests

# Run with Docker
docker-compose -f docker-compose.prod.yml up -d
```

### Environment Variables
```env
# Database
SPRING_DATASOURCE_URL=your_database_url
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password

# Email
SPRING_MAIL_USERNAME=your_email
SPRING_MAIL_PASSWORD=your_app_password

# Redis
SPRING_REDIS_HOST=your_redis_host
SPRING_REDIS_PORT=6379

# Application
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=prod
```

## 🛠️ Development

### Hot Reload Setup
```bash
# Terminal 1: Start CSS watch mode
npm run build:css

# Terminal 2: Start Spring Boot with dev profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Database Commands
```bash
# Connect to SQL Server (Docker)
docker exec -it houserental-db-1 /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P Dat12345!

# Backup database
docker exec houserental-db-1 /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P Dat12345! -Q "BACKUP DATABASE HouseRentalDB TO DISK = '/var/opt/mssql/backup/HouseRentalDB.bak'"
```

### Docker Commands
```bash
# Start services
docker-compose up -d

# View logs
docker-compose logs -f app

# Rebuild with changes
docker-compose up -d --build

# Clean reset
docker-compose down -v && docker-compose up -d
```

## 🧪 Testing

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=AuthServiceTest

# Run with coverage
./mvnw test jacoco:report
```

## 📝 API Documentation

The application provides REST endpoints for:

- **Authentication**: `/api/auth/*`
- **Properties**: `/api/properties/*`
- **Users**: `/api/users/*`  
- **Bookings**: `/api/bookings/*`
- **Admin**: `/api/admin/*`

Visit `/swagger-ui.html` when running locally for interactive API docs.

## 🤝 Contributing

1. **Fork** the repository
2. **Create** a feature branch: `git checkout -b feature/amazing-feature`
3. **Commit** changes: `git commit -m 'Add amazing feature'`
4. **Push** to branch: `git push origin feature/amazing-feature`
5. **Open** a Pull Request

### Development Guidelines
- Follow **Java coding standards**
- Write **unit tests** for new features
- Use **conventional commits**
- Update **documentation** as needed

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- **Your Name** - Initial work - [@yourusername](https://github.com/yourusername)

## 🙋‍♂️ Support

If you have any questions or run into issues:

1. Check the [Issues](https://github.com/yourusername/house-rental/issues) page
2. Create a new issue with detailed description
3. Contact: your.email@example.com

---

**Built with ❤️ using Spring Boot and Tailwind CSS** 