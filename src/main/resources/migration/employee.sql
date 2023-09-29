create table employee (
    id bigserial primary key,
    fullName text,
    email text,
    status text,
    salary float
)

create index full_name_idx on employee(fullName);