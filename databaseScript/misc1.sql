-- Functions
SELECT  p.proname
FROM    pg_catalog.pg_namespace n
JOIN    pg_catalog.pg_proc p
ON      p.pronamespace = n.oid
WHERE   n.nspname = 'public';

-- Relations (FK)
SELECT conrelid::regclass AS table_name,
       conname AS foreign_key,
       pg_get_constraintdef(oid)
FROM   pg_constraint
WHERE  contype = 'f'
AND    connamespace = 'public'::regnamespace
ORDER  BY conrelid::regclass::text, contype DESC;

-- Triggers
SELECT  event_object_table AS table_name ,trigger_name
FROM information_schema.triggers
GROUP BY table_name , trigger_name
ORDER BY table_name ,trigger_name;

ALTER SEQUENCE "BILL_id_seq" RESTART WITH 1;
ALTER SEQUENCE "GUEST_id_seq" RESTART WITH 1;
ALTER SEQUENCE "GUESTKIND_id_seq" RESTART WITH 1;
ALTER SEQUENCE "RENTALFORM_id_seq" RESTART WITH 1;
ALTER SEQUENCE "ROOM_id_seq" RESTART WITH 1;
ALTER SEQUENCE "ROOMKIND_id_seq" RESTART WITH 1;
ALTER SEQUENCE "UNIQUE_id_seq" RESTART WITH 1;
ALTER SEQUENCE "CLOUDINARY_id_seq" RESTART WITH 1;
-- ALTER SEQUENCE "UNIQUE_HISTORY_hid_seq" RESTART WITH 1;

-- create or replace function save_unique_into_unique_history() returns trigger language plpgsql
-- as
-- $$
-- begin
-- 		insert into "public"."UNIQUE_HISTORY"
--     		(id, capacity, coefficient, percentage, start_ordinal, created_at)
--         		values (old.*);
--     return null;
-- end;
-- $$;

-- create or replace trigger on_update_unique after update on "public"."UNIQUE"
-- for each row
-- execute function save_unique_into_unique_history();

-- drop trigger on_update_unique on "public"."UNIQUE"

-- drop function save_unique_into_unique_history();


