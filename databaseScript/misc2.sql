create or replace function set_price_per_day() returns trigger language plpgsql
as
$$
declare
		unique_row "public"."UNIQUE"%rowtype;
    roomkind_price money;
begin
		select * into unique_row from "public"."UNIQUE" limit 1;

		new.price_per_day := unique_row.capacity - unique_row.start_ordinal + 1;
    return new;
end;
$$;









