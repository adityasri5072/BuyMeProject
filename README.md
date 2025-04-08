# BuyMe Project

A web-based auction platform that allows users to buy and sell items through an auction system.

## Features

- User Authentication (Login/Register)
- Auction Creation and Management
- Bidding System
- Admin Dashboard
- Customer Representative Dashboard
- Real-time Notifications
- Similar Items Recommendations
- Support System with Questions and Answers
- User Profile Management
- Auction Reports and Analytics

## Technology Stack

- Java
- JSP (JavaServer Pages)
- Servlets
- MySQL Database
- HTML/CSS
- JavaScript

## Prerequisites

- Java JDK 8 or higher
- MySQL Database
- Apache Tomcat Server
- Maven (for dependency management)

## Setup Instructions

1. Clone the repository:
   ```bash
   git clone https://github.com/adityasri5072/BuyMeProject.git
   ```

2. Set up the database:
   - Create a MySQL database
   - Update the database configuration in `src/main/java/com/buymeproject/database/DBConfig.java`

3. Configure the application:
   - Update the database connection settings if needed
   - Configure your Tomcat server settings

4. Deploy the application:
   - Build the project using Maven
   - Deploy the WAR file to your Tomcat server

## Project Structure

- `src/main/java/com/buymeproject/` - Java source files
- `src/main/webapp/` - Web resources (JSP, CSS, JavaScript)
- `src/main/webapp/WEB-INF/` - Web application configuration
- `src/main/webapp/pictures/` - Image assets

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 