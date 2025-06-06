# Distopy Market

## Minimum Requirements
- Java 17+
- Maven
- Oracle Database
- Email account to SMTP
### Data Model

#### Entities

1. **UserDtls**
   - Fields: id, name, mobileNumber, email, address, city, state, pincode, password, profileImage, role, isEnabled, accountNonLocked, failedLoginCount, lockTime, resetToken
   - Purpose: Stores user information, authentication details, and account security status

2. **Category**
   - Fields: id, name, imageName, isActive
   - Purpose: Represents product categories

3. **Product**
   - Fields: id, title, description, category, price, stock, image, discount, discountPrice, isActive
   - Purpose: Stores product information including pricing and inventory

4. **Cart**
   - Fields: id, user, product, quantity, totalPrice (transient), totalOrderPrice (transient)
   - Purpose: Represents items in a user's shopping cart

5. **OrderAddress**
   - Fields: id, firstName, lastName, email, mobileNumber, address, city, state, pincode
   - Purpose: Stores shipping address information for orders

6. **ProductOrder**
   - Fields: id, orderId, orderDate, product, price, quantity, user, status, paymentType, orderAddress
   - Purpose: Represents a customer order with product, pricing, and shipping details

7. **OrderRequest** (DTO)
   - Fields: firstName, lastName, email, mobileNumber, address, city, state, pincode, paymentType
   - Purpose: Data transfer object for order creation requests

#### Entity Relationships

1. **UserDtls - Cart**: One-to-Many
   - A user can have multiple cart items
   - Each cart item belongs to one user

2. **Product - Cart**: One-to-Many
   - A product can be in multiple cart items
   - Each cart item contains one product

3. **Category - Product**: Indirect Relationship
   - Products have a category field (stored as String)
   - Categories are managed separately

4. **UserDtls - ProductOrder**: One-to-Many
   - A user can have multiple orders
   - Each order belongs to one user

5. **Product - ProductOrder**: One-to-Many
   - A product can be in multiple orders
   - Each order item references one product

6. **OrderAddress - ProductOrder**: One-to-One
   - Each order has one shipping address
   - Each shipping address belongs to one order

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
