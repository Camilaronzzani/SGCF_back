-- Fix enum case mismatches in existing data

-- Update customer table - handle various case formats
UPDATE customer 
SET country_of_customer = CASE
    WHEN LOWER(country_of_customer) = 'brazil' THEN 'BRAZIL'
    WHEN LOWER(country_of_customer) = 'paraguay' THEN 'PARAGUAY'
    WHEN LOWER(country_of_customer) = 'united_states' THEN 'UNITED_STATES'
    WHEN LOWER(country_of_customer) = 'europe' THEN 'EUROPE'
    ELSE country_of_customer
END
WHERE country_of_customer IS NOT NULL;

-- Update tour table
UPDATE tour 
SET country_of_tour = CASE
    WHEN LOWER(country_of_tour) = 'argentina' THEN 'ARGENTINA'
    WHEN LOWER(country_of_tour) = 'brazil' THEN 'BRAZIL'
    WHEN LOWER(country_of_tour) = 'paraguay' THEN 'PARAGUAY'
    ELSE country_of_tour
END
WHERE country_of_tour IS NOT NULL;

-- Update payment table
UPDATE payment 
SET status = CASE
    WHEN LOWER(status) = 'pending' THEN 'PENDING'
    WHEN LOWER(status) IN ('cancelled', 'canceled', 'cancelled') THEN 'CANCELLED'
    WHEN LOWER(status) = 'confirmed' THEN 'CONFIRMED'
    ELSE status
END
WHERE status IS NOT NULL;

-- Update reservation table
UPDATE reservation 
SET status = CASE
    WHEN LOWER(status) = 'pending' THEN 'PENDING'
    WHEN LOWER(status) IN ('cancelled', 'canceled', 'cancelled') THEN 'CANCELLED'
    WHEN LOWER(status) = 'confirmed' THEN 'CONFIRMED'
    ELSE status
END
WHERE status IS NOT NULL;

-- Update user table
UPDATE "user" 
SET permission = CASE
    WHEN LOWER(permission) = 'manager' THEN 'MANAGER'
    WHEN LOWER(permission) = 'employee' THEN 'EMPLOYEE'
    ELSE permission
END
WHERE permission IS NOT NULL;

-- Update clientes_languages table
UPDATE clientes_languages 
SET language = CASE
    WHEN LOWER(language) = 'spanish' THEN 'SPANISH'
    WHEN LOWER(language) = 'portuguese' THEN 'PORTUGUESE'
    WHEN LOWER(language) = 'english' THEN 'ENGLISH'
    ELSE language
END
WHERE language IS NOT NULL;

-- Update languages_funcionario table
UPDATE languages_funcionario 
SET language = CASE
    WHEN LOWER(language) = 'spanish' THEN 'SPANISH'
    WHEN LOWER(language) = 'portuguese' THEN 'PORTUGUESE'
    WHEN LOWER(language) = 'english' THEN 'ENGLISH'
    ELSE language
END
WHERE language IS NOT NULL;
