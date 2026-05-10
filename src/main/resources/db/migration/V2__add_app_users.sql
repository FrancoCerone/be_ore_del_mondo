-- Ripara DB dove V1 era stata applicata prima che app_users fosse inclusa in init,
-- oppure dopo reset parziale dello schema. Idempotente se la tabella esiste già.
create table if not exists app_users (
    id bigserial primary key,
    uuid uuid not null unique,
    email varchar(180) not null unique,
    password_hash varchar(255) not null,
    role varchar(30) not null,
    enabled boolean not null default true,
    created_at timestamptz not null default now()
);
