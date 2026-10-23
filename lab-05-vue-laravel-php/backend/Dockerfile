FROM composer:2.10 AS composer
FROM php:8.5.11-cli-alpine
WORKDIR /var/www
COPY --from=composer /usr/bin/composer /usr/bin/composer
RUN apk add --no-cache libzip unzip \
    && apk add --no-cache --virtual .build-deps $PHPIZE_DEPS libzip-dev linux-headers \
    && docker-php-ext-install pdo_mysql zip \
    && pecl install xdebug-3.5.3 && docker-php-ext-enable xdebug \
    && apk del .build-deps
COPY xdebug.ini /usr/local/etc/php/conf.d/99-xdebug.ini
COPY composer.json composer.lock ./
RUN composer install --no-interaction --prefer-dist --no-progress
COPY . .
CMD ["php","-S","0.0.0.0:8000","-t","public"]
