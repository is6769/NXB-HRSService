--liquibase formatted sql
--changeset is6769:0002-create-initial-tariffs

insert into tariffs(name,description,cycle_size,is_active)
values (
        'Классика',
        'Классика',
        '30 days',
        TRUE
       );

insert into service_packages(name,description,service_type)
values (
        'Пакет минут «Классика»',
        'Пакет минут для тарифа «Классика»',
        'MINUTES'
       );

insert into package_rules(service_package_id,rule_type,value,unit,condition)
values (1,
        'RATE',
        1.5,
        'у.е.',
        '{"condition_type": "simple", "field": "destination_operator", "operator": "equals", "value": "Ромашка"}'
        );

insert into package_rules(service_package_id,rule_type,value,unit,condition)
values (
        1,
        'RATE',
        2.5,
        'у.е.',
        '{"condition_type": "simple", "field": "destination_operator", "operator": "not_equals", "value": "Ромашка"}');


INSERT INTO subscriber_tariff (subscriber_id, tariff_id, cycle_start, cycle_end)
VALUES (
           1,
           1,
           LOCALTIMESTAMP - interval '1 year',
           LOCALTIMESTAMP - interval '1 year' + (SELECT cycle_size::interval FROM tariffs WHERE id = 1)
       );

INSERT INTO tariff_packages(tariff_id,service_package_id,priority)
values (
            1,
            1,
            1
       );

