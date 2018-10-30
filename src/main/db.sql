select * from product;

alter table product add discount_string varchar;
alter table product add marked_for_delete tinyint;


commit ;

select * from product where id = '707f8e1b7cb6f246e01fd2be360b272d';

select count(*) from product;
