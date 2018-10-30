package com.futuretex.repository;

import java.util.List;

import com.futuretex.domain.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IProductRepository extends CrudRepository<Product, String> {

	@Query("SELECT p FROM Product p WHERE p.markedForDelete = false and p.title like %:title% order by p.percentageDiscount desc")
	List<Product> searchByTitle(@Param("title") String title);
}