FROM php:8.2-fpm

# Set working directory
WORKDIR /var/www

# Install dependencies for the project
RUN apt-get update && apt-get install -y libpq-dev && docker-php-ext-install pdo pdo_pgsql

# Install Composer globally
COPY --from=composer:latest /usr/bin/composer /usr/bin/composer

# Copy existing application directory contents
COPY . /var/www

# Install PHP dependencies with Composer
RUN composer install --no-interaction --no-plugins --no-scripts --prefer-dist

# Copy existing application directory permissions
COPY --chown=www-data:www-data . /var/www

# Ensure files are owned by the www-data user
RUN chown -R www-data:www-data /var/www

# Change timezone - Adjust if necessary
ENV TZ=Europe/Warsaw
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Expose port 9000 and start php-fpm server
EXPOSE 9000
CMD ["php-fpm"]
