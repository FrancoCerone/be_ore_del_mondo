create table watches (
    id bigserial primary key,
    uuid uuid not null unique,
    name varchar(180) not null,
    slug varchar(220) not null unique,
    description text,
    short_description varchar(500),
    brand varchar(120) not null,
    model varchar(120) not null,
    price numeric(12, 2) not null check (price > 0),
    currency varchar(3) not null default 'EUR',
    condition varchar(30) not null,
    production_year integer check (production_year between 1800 and 2100),
    reference_number varchar(120),
    movement varchar(30),
    case_material varchar(120),
    strap_material varchar(120),
    diameter numeric(5, 2) check (diameter is null or diameter > 0),
    water_resistance varchar(80),
    stock integer not null default 0 check (stock >= 0),
    featured boolean not null default false,
    published boolean not null default false,
    seo_title varchar(180),
    seo_description varchar(320),
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create table watch_images (
    id bigserial primary key,
    watch_id bigint not null references watches(id) on delete cascade,
    image_url varchar(1000) not null,
    alt_text varchar(255),
    sort_order integer not null default 0 check (sort_order >= 0),
    is_cover boolean not null default false
);

create table app_users (
    id bigserial primary key,
    uuid uuid not null unique,
    email varchar(180) not null unique,
    password_hash varchar(255) not null,
    role varchar(30) not null,
    enabled boolean not null default true,
    created_at timestamptz not null default now()
);

create unique index ux_watch_images_single_cover
    on watch_images(watch_id)
    where is_cover;

create index idx_watches_published_created_at on watches(published, created_at desc);
create index idx_watches_featured_published on watches(featured, published);
create index idx_watches_brand_lower on watches(lower(brand));
create index idx_watches_price on watches(price);
create index idx_watches_search_lower on watches(lower(name), lower(brand), lower(model));
create index idx_watch_images_watch_order on watch_images(watch_id, sort_order);

create or replace function set_updated_at()
returns trigger as $$
begin
    new.updated_at = now();
    return new;
end;
$$ language plpgsql;

create trigger watches_set_updated_at
before update on watches
for each row
execute function set_updated_at();
