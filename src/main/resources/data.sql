CREATE TABLE debt_management.debt_sys_user
(
    email      varchar(150) primary key,
    name       varchar(200) not null,
    salary     double precision,
    savings    double precision,
    created_at timestamp,
    updated_at timestamp,
    active     BOOLEAN default TRUE
);

-- FIXED EXPENSES
CREATE TABLE debt_management.fixed_expense_catalog
(
    id   serial primary key,
    name varchar(250)
);

CREATE TABLE debt_management.fixed_expense
(
    id                     serial primary key,
    name                   varchar          not null,
    monthly_cost           double precision not null,
    payment_day            integer          not null,
    active                 BOOLEAN default TRUE,
    fixed_expenses_catalog int              not null,
    debt_sys_user          varchar(150)     not null,
    foreign key (fixed_expenses_catalog) references debt_management.fixed_expense_catalog (id),
    foreign key (debt_sys_user) references debt_management.debt_sys_user (email)
);

-- FINANCIAL ENTITY
CREATE TABLE debt_management.financial_provider_catalog
(
    id   serial primary key not null,
    name varchar(250)
);

CREATE TABLE debt_management.financial_provider
(
    code                       varchar(100) not null primary key,
    name                       varchar(250) not null,
    created_at                 timestamp,
    updated_at                 timestamp,
    active                     BOOLEAN default TRUE,
    financial_provider_catalog integer      not null,
    debt_sys_user              varchar(150) not null,
    foreign key (financial_provider_catalog) references debt_management.financial_provider_catalog (id),
    foreign key (debt_sys_user) references debt_management.debt_sys_user (email)
);

-- DEBT ACCOUNT
CREATE TABLE debt_management.debt_account
(
    code                   varchar(150)     not null primary key,
    name                   varchar(250)     not null,
    pay_day                integer          not null,
    credit                 double precision not null,
    created_at             timestamp,
    updated_at             timestamp,
    active                 BOOLEAN default TRUE,
    account_statement_type varchar(200)     not null,
    financial_provider     varchar(100)     not null,
    foreign key (financial_provider) references debt_management.financial_provider (code)
);

-- DEBT
CREATE TABLE debt_management.debt
(
    id                  serial primary key not null,
    description         varchar(300)       not null,
    operation_date      varchar(150)       not null,
    current_installment integer            not null,
    max_financing_term  integer            not null,
    original_amount     double precision   not null,
    monthly_payment     double precision   not null,
    created_at          timestamp,
    updated_at          timestamp,
    active              BOOLEAN default TRUE,
    hash_sum            varchar(64),
    debt_account        varchar(150)       not null,
    foreign key (debt_account) references debt_management.debt_account (code)
);

