-- Fix enum check constraints to match uppercase Java enum values

-- Fix customer.country_of_customer constraint
ALTER TABLE "customer" DROP CONSTRAINT IF EXISTS "customer_country_of_customer_check";
ALTER TABLE "customer" ADD CONSTRAINT "customer_country_of_customer_check"
    CHECK ("country_of_customer" IN (
        'BRAZIL',
        'PARAGUAY',
        'UNITED_STATES',
        'EUROPE'
    ));

-- Fix tour.country_of_tour constraint
ALTER TABLE "tour" DROP CONSTRAINT IF EXISTS "tour_country_of_tour_check";
ALTER TABLE "tour" ADD CONSTRAINT "tour_country_of_tour_check"
    CHECK ("country_of_tour" IN (
        'ARGENTINA',
        'BRAZIL',
        'PARAGUAY'
    ));

-- Fix payment.status constraint
ALTER TABLE "payment" DROP CONSTRAINT IF EXISTS "payment_status_check";
ALTER TABLE "payment" ADD CONSTRAINT "payment_status_check"
    CHECK ("status" IN (
        'PENDING',
        'CANCELLED',
        'CONFIRMED'
    ));

-- Fix reservation.status constraint
ALTER TABLE "reservation" DROP CONSTRAINT IF EXISTS "reservation_status_check";
ALTER TABLE "reservation" ADD CONSTRAINT "reservation_status_check"
    CHECK ("status" IN (
        'PENDING',
        'CANCELLED',
        'CONFIRMED'
    ));

-- Fix user.permission constraint
ALTER TABLE "user" DROP CONSTRAINT IF EXISTS "user_permission_check";
ALTER TABLE "user" ADD CONSTRAINT "user_permission_check"
    CHECK ("permission" IN (
        'MANAGER',
        'EMPLOYEE'
    ));

-- Fix clientes_languages.language constraint
ALTER TABLE "clientes_languages" DROP CONSTRAINT IF EXISTS "clientes_languages_language_check";
ALTER TABLE "clientes_languages" ADD CONSTRAINT "clientes_languages_language_check"
    CHECK ("language" IN (
        'SPANISH',
        'PORTUGUESE',
        'ENGLISH'
    ));

-- Fix languages_funcionario.language constraint
ALTER TABLE "languages_funcionario" DROP CONSTRAINT IF EXISTS "languages_funcionario_language_check";
ALTER TABLE "languages_funcionario" ADD CONSTRAINT "languages_funcionario_language_check"
    CHECK ("language" IN (
        'SPANISH',
        'PORTUGUESE',
        'ENGLISH'
    ));
