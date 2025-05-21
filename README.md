# Distopy Market

## Minimum Requirements
- Java 17+
- Maven
- Oracle Database
- Email account to SMTP

## Data Model

```
+----------------+       +----------------+       +----------------+
|    UserDtls    |       |    Product     |       |    Category    |
+----------------+       +----------------+       +----------------+
| id: Integer    |       | id: int        |       | id: int        |
| name: String   |       | title: String  |       | name: String   |
| email: String  |       | description:   |       | imageName:     |
| password:      |       |   String       |       |   String       |
|   String       |       | category:      |       | isActive:      |
| mobileNumber:  |       |   String       |       |   Boolean      |
|   String       |       | price: Double  |       +----------------+
| address:       |       | stock: int     |
|   String       |       | image: String  |
| city: String   |       | discount:      |
| state: String  |       |   Integer      |
| pincode:       |       | discountPrice: |
|   String       |       |   Double       |
| profileImage:  |       | isActive:      |
|   String       |       |   Boolean      |
| role: String   |       +----------------+
| isEnabled:     |               |
|   Boolean      |               |
| accountNon     |               |
|   Locked:      |               |
|   Boolean      |               |
| failedLogin    |               |
|   Count:       |               |
|   Integer      |               |
| lockTime: Date |               |
| resetToken:    |               |
|   String       |               |
+----------------+               |
        |                        |
        |                        |
        |                        |
        v                        v
+----------------+
|      Cart      |
+----------------+
| id: Integer    |
| user: UserDtls |
| product:       |
|   Product      |
| quantity:      |
|   Integer      |
| totalPrice:    |
|   Double       |
+----------------+
```

### Entity Relationships

1. **UserDtls - Cart**: One-to-Many
   - A user can have multiple cart items
   - Each cart item belongs to one user

2. **Product - Cart**: One-to-Many
   - A product can be in multiple cart items
   - Each cart item contains one product

3. **Category - Product**: Indirect Relationship
   - Products have a category field (stored as String)
   - Categories are managed separately

### Key Features

- User authentication and authorization with role-based access
- Product catalog with categories
- Shopping cart functionality
- Account security features (locking, password reset)

## Project Structure

```
src/
  ├── main/
  │   ├── java/
  │   │   └── com/distopy/
  │   └── resources/
  │       ├── templates/
  │       └── application.properties.template
  └── test/
```

## Configuration and Sensitive Information

### Overview
This project has been configured to protect sensitive information such as database credentials and email settings. The following files are used for configuration:

1. `application.properties.template` - A template file with placeholders that can be committed to version control
2. `application.properties` - The main configuration file with placeholders and default values
3. `application-dev.properties` - A development-specific configuration file with actual values (not committed to version control)

### Setup Instructions for Developers

#### Option 1: Using application-dev.properties (Recommended for Development)
1. Create a copy of `application-dev.properties` if it doesn't exist
2. Add your actual database and email credentials to this file
3. Uncomment the line `spring.profiles.active=dev` in `application.properties`
4. This file will not be committed to version control (it's in .gitignore)

#### Option 2: Using Environment Variables
1. Set the following environment variables on your system:
   - `DB_URL` - Database URL
   - `DB_USERNAME` - Database username
   - `DB_PASSWORD` - Database password
   - `MAIL_HOST` - Mail server host
   - `MAIL_USERNAME` - Mail server username
   - `MAIL_PASSWORD` - Mail server password
   - `MAIL_PORT` - Mail server port

#### Option 3: Using .env File (For tools that support it)
1. Create a `.env` file at the root of the project
2. Add your environment variables in the format `KEY=VALUE`
3. This file will not be committed to version control (it's in .gitignore)

### Important Notes
- Never commit sensitive information like passwords or API keys to version control
- The `application.properties` file in the repository uses placeholders with default values
- For production deployment, use environment variables or a secure configuration management system
- If you add new sensitive configuration values, update the template file and documentation
