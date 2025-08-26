# SmartHealth

SmartHealth project containing:

- **Backend (Spring Boot)** → `SmartHealth/`
- **User Frontend (HTML, CSS and JavaScript)** → `sh/`
- **Admin Frontend (HTML, CSS and JavaScript)** → `admin/`

## 📂 Structure
SmartHealth/
├─ SmartHealth/ → Spring Boot backend
├─ sh/ → User frontend
├─ admin/ → Admin frontend

## 🚀 Getting Started

### Backend (Spring Boot)
1. Import the Gradle project **SmartHealth** into your IDE (STS, Eclipse, IntelliJ). (Optional)
2. Open `src/main/resources/application.properties` and configure the following:
   - **Database Connection**: Update `spring.datasource.driver-class-name`, `spring.datasource.url`, `spring.datasource.username`, and `spring.datasource.password` according to your database setup.
   - **Image Storage Path**: Set `smarthealth.paths.image-storage-path` to the desired location for storing profile images.
   - **CORS Configuration**: Update properties starting with `smarthealth.cors` to allow cross-origin requests as per your requirements.
   - **Other Properties**: Adjust settings like server port, log file path, etc. as needed.
3. Run the application:
   - **From IDE**: Execute the `main` method in `SmartHealthApplication` class.
   - **From Command Line**:  
     - Navigate to the **SmartHealth/** directory.  
     - Build the project:  
       ```bash
       ./gradlew clean build
       ```
       (Use `./gradlew clean build -x test` to skip tests.)  
     - After a successful build, navigate to `build/libs/` and run:  
       ```bash
       java -jar SmartHealth-1.0.0.jar
       ```

### Common Issues & Fixes
1. **Database Connection Errors** (e.g., `JDBCConnectionException`, driver not found, connection refused):  
   - Verify database driver, URL, host, port, username, and password.  
   - Ensure the database server is running and accessible.  

2. **Mapper Class Dependency Issues** (when running from IDE):  
   - These are usually caused by missing generated `MapperImpl` classes. You can resolve this by either:  
     a) Running  
        ```bash
        ./gradlew clean build
        ```  
        and checking if the `MapperImpl` classes are generated under  
        `build/generated/sources/annotationProcessor/java/main/com/ps/mapper/`.  
        If generated, the application should start.  
     b) Enabling **Annotation Processing** in your IDE settings.  

   > ⚡ Tip: If you frequently modify mapper interfaces, enabling annotation processing in your IDE is recommended. Otherwise, you’ll need to rebuild the project every time via Gradle.

---
### Frontend (User & Admin)

1. **Simple Hosting**  
   - Copy the `sh/` (User Frontend) and `admin/` (Admin Frontend) folders to your hosting server.  
   - Example: If you’re using **XAMPP**, place both folders inside `\xampp\htdocs\`.  
   - Start Apache and access the applications via:  
     - [http://localhost/sh](http://localhost/sh)  
     - [http://localhost/admin](http://localhost/admin)  
     (or based on your server configuration)

2. **Using Virtual Hosts (Recommended for cleaner URLs)**  
   - You can configure custom domain mappings for better accessibility.  
   - Example: If your project resides in `F:/Projects/SmartHealth` and you’re using **XAMPP**, add the following configuration to  
     `\xampp\apache\conf\extra\httpd-vhosts.conf`:  

     ```apache
     <VirtualHost *:80>
         ServerName smarthealth
         DocumentRoot "F:/Projects/SmartHealth/sh"
         <Directory "F:/Projects/SmartHealth/sh">
             Options Indexes FollowSymLinks Includes ExecCGI
             AllowOverride All
             Require all granted
         </Directory>
     </VirtualHost>

     <VirtualHost *:80>
         ServerName admin.sh
         DocumentRoot "F:/Projects/SmartHealth/admin"
         <Directory "F:/Projects/SmartHealth/admin">
             Options Indexes FollowSymLinks Includes ExecCGI
             AllowOverride All
             Require all granted
         </Directory>
     </VirtualHost>
     ```

   - After saving the configuration:  
     1. Restart Apache server.  
     2. Update your system’s `hosts` file to map domains:  
        ```
        127.0.0.1   smarthealth
        127.0.0.1   admin.sh
        ```
     3. Access the apps via:  
        - [http://smarthealth](http://smarthealth)  
        - [http://admin.sh](http://admin.sh)  
        (or according to your chosen domain names)