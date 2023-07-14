create or replace function set_new_updated_timestamp() returns trigger language plpgsql
as
$$
begin
-- 		insert into "public"."UNIQUE_HISTORY"
--     		(id, capacity, coefficient, percentage, start_ordinal, created_at)
--         		values (old.*);
-- 		return null;
		new.updated_at := now();
    return new;
end;
$$;

create or replace trigger on_update_set_new_updated_timestamp before update on "public"."UNIQUE"
for each row
execute function set_new_updated_timestamp();

create or replace trigger on_update_set_new_updated_timestamp before update on "public"."ROOM"
for each row
execute function set_new_updated_timestamp();

create or replace trigger on_update_set_new_updated_timestamp before update on "public"."ROOMKIND"
for each row
execute function set_new_updated_timestamp();

create or replace trigger on_update_set_new_updated_timestamp before update on "public"."GUEST"
for each row
execute function set_new_updated_timestamp();

create or replace trigger on_update_set_new_updated_timestamp before update on "public"."GUESTKIND"
for each row
execute function set_new_updated_timestamp();

create or replace trigger on_update_set_new_updated_timestamp before update on "public"."BILL"
for each row
execute function set_new_updated_timestamp();

create or replace trigger on_update_set_new_updated_timestamp before update on "public"."RENTALFORM"
for each row
execute function set_new_updated_timestamp();

create or replace trigger on_update_set_new_updated_timestamp before update on "public"."CLOUDINARY"
for each row
execute function set_new_updated_timestamp();

-- create or replace trigger on_update_set_new_updated_timestamp before update on "public"."RENTALFORM_GUEST"
-- for each row
-- execute function set_new_updated_timestamp();

create or replace trigger on_update_set_new_updated_timestamp before update on "public"."USER"
for each row
execute function set_new_updated_timestamp();













DROP FUNCTION IF EXISTS set_room_is_occupied_true();
DROP FUNCTION IF EXISTS set_room_is_occupied_false();
DROP FUNCTION IF EXISTS set_new_room_is_occupied_true();
DROP FUNCTION IF EXISTS set_old_room_is_occupied_false();
DROP FUNCTION IF EXISTS set_new_room_is_occupied_true_and_old_room_is_occupied_false();
DROP TRIGGER IF EXISTS on_insert_set_new_room_is_occupied_true ON "public"."RENTALFORM";
DROP TRIGGER IF EXISTS on_delete_set_old_room_is_occupied_false ON "public"."RENTALFORM";
DROP TRIGGER IF EXISTS on_update_set_new_room_is_occupied_true_and_old_room_is_occupied_false ON "public"."RENTALFORM";



create or replace function set_new_room_is_occupied_true () returns trigger language plpgsql
as
$$
begin
		update "public"."ROOM" set is_occupied = true  where id = new.room_id;
    return new;
end;
$$;

create or replace function set_old_room_is_occupied_false() returns trigger language plpgsql
as
$$
begin
		update "public"."ROOM" set is_occupied = false where id = old.room_id;
    return old;
end;
$$;

create or replace function set_new_room_is_occupied_true_and_old_room_is_occupied_false() returns trigger language plpgsql
as
$$
begin

		if new.is_resolved = true     then begin
    	 update "public"."ROOM" set is_occupied = false where id = old.room_id; end; else end if;

		if old.room_id <> new.room_id then begin
    	 update "public"."ROOM" set is_occupied = false where id = old.room_id;
    	 update "public"."ROOM" set is_occupied = true  where id = new.room_id; end; else end if;

    return new;

end;
$$;

create or replace trigger on_insert_set_new_room_is_occupied_true  before insert on "public"."RENTALFORM"
for each row
execute function set_new_room_is_occupied_true ();

create or replace trigger on_delete_set_old_room_is_occupied_false before delete on "public"."RENTALFORM"
for each row
execute function set_old_room_is_occupied_false();

create or replace trigger on_update_set_new_room_is_occupied_true_and_old_room_is_occupied_false before update on "public"."RENTALFORM"
for each row
execute function set_new_room_is_occupied_true_and_old_room_is_occupied_false();







create or replace function set_old_rental_form_bill_id_and_is_resolved_true() returns trigger language plpgsql
as
$$
begin
		update "public"."RENTALFORM" set bill_id = new.id, is_resolved = true where guest_id = new.guest_id and is_resolved = false;
    return new;
end;
$$;

create or replace trigger on_insert_set_old_rental_form_bill_id_and_is_resolved_true after insert on "public"."BILL"
for each row
execute function set_old_rental_form_bill_id_and_is_resolved_true();