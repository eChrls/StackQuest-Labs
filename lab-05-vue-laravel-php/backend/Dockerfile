FROM composer:2.8 AS composer
FROM php:8.3-cli
WORKDIR /var/www
COPY --from=composer /usr/bin/composer /usr/bin/composer
RUN apt-get update && apt-get install -y --no-install-recommends libzip-dev unzip && docker-php-ext-install pdo_mysql zip && rm -rf /var/lib/apt/lists/*
RUN pecl install xdebug-3.5.3 && docker-php-ext-enable xdebug
COPY xdebug.ini /usr/local/etc/php/conf.d/99-xdebug.ini
COPY composer.json composer.lock ./
RUN composer install --no-interaction --prefer-dist --no-progress
COPY . .
CMD ["php","-S","0.0.0.0:8000","-t","public"]
