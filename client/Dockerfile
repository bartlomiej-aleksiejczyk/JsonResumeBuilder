FROM php:8.2-fpm

WORKDIR /var/www

RUN apt-get update && apt-get install -y \
    libpq-dev \
    git && \
    docker-php-ext-install pdo pdo_pgsql sockets

COPY --from=composer:latest /usr/bin/composer /usr/bin/composer

COPY . /var/www

RUN composer install --no-interaction --no-plugins --no-scripts --prefer-dist

RUN chown -R www-data:www-data /var/www

ENV TZ=Europe/Warsaw
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

EXPOSE 9000
CMD ["php-fpm"]
