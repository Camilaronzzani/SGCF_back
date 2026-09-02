create table "customer" (
                            "id" bigint ,
                            "active" boolean,
                            "cnpj" varchar(15),
                            "country_of_customer" varchar(255)
                                check ("country_of_customer" in (
                                                                 'brazil',
                                                                 'paraguay',
                                                                 'united_states',
                                                                 'europe'
                                    )),
                            "cpf" varchar(11),
                            "user_email" varchar(100) not null,
                            "name" varchar(255),

                            primary key ("id"),

                            constraint "uk_customer_cnpj"
                                unique ("cnpj"),

                            constraint "uk_customer_cpf"
                                unique ("cpf"),

                            constraint "uk_customer_email"
                                unique ("user_email")
);

create table "employee" (
                            "id" bigint ,
                            "active" boolean,
                            "cpf" varchar(11) not null,
                            "day_of_birth" date,
                            "name" varchar(255) not null,

                            primary key ("id"),

                            constraint "uk_employee_cpf"
                                unique ("cpf")
);

create table "tour" (
                        "id" bigint ,
                        "active" boolean,
                        "country_of_tour" varchar(255)
                            check ("country_of_tour" in (
                                                         'argentina',
                                                         'brazil',
                                                         'paraguay'
                                )),
                        "km_of_tour" bigint,
                        "locations" varchar(255),
                        "name" varchar(255),
                        "price" double precision,

                        primary key ("id")
);

create table "payment" (
                           "id" bigint ,
                           "active" boolean,
                           "status" varchar(255) not null
                               check ("status" in (
                                                   'pending',
                                                   'cancelled',
                                                   'confirmed'
                                   )),
                           "total_account" double precision,
                           "fk_customer_id" bigint not null,

                           primary key ("id"),

                           constraint "fk_payment_customer"
                               foreign key ("fk_customer_id")
                                   references "customer" ("id")
);

create table "reservation" (
                               "id" bigint ,
                               "active" boolean,
                               "date" date,
                               "status" varchar(255) not null
                                   check ("status" in (
                                                       'pending',
                                                       'cancelled',
                                                       'confirmed'
                                       )),
                               "value" double precision not null,
                               "fk_customer_id" bigint not null,
                               "fk_employee_id" bigint not null,
                               "fk_tour_id" bigint not null,

                               primary key ("id"),

                               constraint "fk_reservation_customer"
                                   foreign key ("fk_customer_id")
                                       references "customer" ("id"),

                               constraint "fk_reservation_employee"
                                   foreign key ("fk_employee_id")
                                       references "employee" ("id"),

                               constraint "fk_reservation_tour"
                                   foreign key ("fk_tour_id")
                                       references "tour" ("id")
);

create table "quota" (
                         "id" bigint ,
                         "active" boolean,
                         "end_date" date,
                         "start_date" date,
                         "target_value" double precision,
                         "fk_employee_id" bigint,

                         primary key ("id"),

                         constraint "fk_quota_employee"
                             foreign key ("fk_employee_id")
                                 references "employee" ("id")
);

create table "user" (
                        "id" bigint ,
                        "active" boolean,
                        "user_email" varchar(100) not null,
                        "permission" varchar(255) not null
                            check ("permission" in (
                                                    'manager',
                                                    'employee'
                                )),
                        "user_name" varchar(35) not null,
                        "user_password" varchar(255) not null,
                        "fk_employee_id" bigint,

                        primary key ("id"),

                        constraint "uk_user_email"
                            unique ("user_email"),

                        constraint "uk_user_name"
                            unique ("user_name"),

                        constraint "uk_user_employee"
                            unique ("fk_employee_id"),

                        constraint "fk_user_employee"
                            foreign key ("fk_employee_id")
                                references "employee" ("id")
);

create table "password_reset" (
                                  "id" bigint ,
                                  "expiration" timestamp(6) not null,
                                  "token" varchar(255) not null,
                                  "used" boolean not null,
                                  "user_id" bigint,

                                  primary key ("id"),

                                  constraint "fk_password_reset_user"
                                      foreign key ("user_id")
                                          references "user" ("id")
);

create table "clientes_languages" (
                                      "fk_clientes_id" bigint not null,
                                      "language" varchar(255)
                                          check ("language" in (
                                                                'spanish',
                                                                'portuguese',
                                                                'english'
                                              )),

                                      constraint "fk_clientes_languages_customer"
                                          foreign key ("fk_clientes_id")
                                              references "customer" ("id")
);

create table "languages_funcionario" (
                                         "fk_funcionario_id" bigint not null,
                                         "language" varchar(255)
                                             check ("language" in (
                                                                   'spanish',
                                                                   'portuguese',
                                                                   'english'
                                                 )),

                                         constraint "fk_languages_employee"
                                             foreign key ("fk_funcionario_id")
                                                 references "employee" ("id")
);