--liquibase formatted sql
--changeset is6769:0002-create-initial-tariffs


--                                                          Классика
insert into tariffs(name,description,cycle_size,is_active)
values (
        'Классика',
        'Классика',
        '0 days',
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
        '{"type": "and", "conditions": [
            {"field": "call_type", "operator": "equals", "value": "01"},
            {"field": "other_operator", "operator": "equals", "value": "Ромашка"}
          ]
        }'
        );

insert into package_rules(service_package_id,rule_type,value,unit,condition)
values (1,
        'RATE',
        2.5,
        'у.е.',
        '{"type": "and", "conditions": [
          {"type": "field", "field": "call_type", "operator": "equals", "value": "01"},
          {"type": "field", "field": "other_operator", "operator": "not_equals", "value": "Ромашка"}
        ]
        }'
       );

insert into package_rules(service_package_id,rule_type,value,unit,condition)
values (
        1,
        'RATE',
        0,
        'у.е.',
        '{"type": "field", "field": "call_type", "operator": "equals", "value": "02"}'
       );


-- INSERT INTO subscriber_tariff (subscriber_id, tariff_id, cycle_start, cycle_end)
-- VALUES (
--            1,
--            1,
--            LOCALTIMESTAMP - interval '1 year',
--            LOCALTIMESTAMP - interval '1 year' + (SELECT cycle_size::interval FROM tariffs WHERE id = 1)
--        );

INSERT INTO tariff_packages(tariff_id,service_package_id,priority)
values (
            1,
            1,
            1
       );

--                                                                  Помесячный

insert into tariffs(name,description,cycle_size,is_active)
values (
           'Помесячный',
           'Помесячный',
           '30 days',
           TRUE
       );

insert into service_packages(name,description,service_type)
values (
           'Пакет минут «Помесячный»',
           'Пакет минут для тарифа «Помесячный»',
           'MINUTES'
       );

insert into package_rules(service_package_id,rule_type,value,unit,condition)
values (2,
        'LIMIT',
        50,
        'minutes',
        '{"type": "always_true"}'
       );

insert into package_rules(service_package_id,rule_type,value,unit,condition)
values (2,
        'RATE',
        0,
        'y.e.',
        '{"type": "always_true"}'
       );


insert into package_rules(service_package_id,rule_type,value,unit,condition)
values (
           2,
           'COST',
           100,
           'у.е.',
           '{"type": "always_true"}'
       );


-- INSERT INTO subscriber_tariff (subscriber_id, tariff_id, cycle_start, cycle_end)
-- VALUES (
--            1,
--            1,
--            LOCALTIMESTAMP - interval '1 year',
--            LOCALTIMESTAMP - interval '1 year' + (SELECT cycle_size::interval FROM tariffs WHERE id = 1)
--        );

INSERT INTO tariff_packages(tariff_id,service_package_id,priority)
values (
           2,
           2,
           1
       );

INSERT INTO tariff_packages(tariff_id,service_package_id,priority)
values (
           2,
           1,
           2
       );

