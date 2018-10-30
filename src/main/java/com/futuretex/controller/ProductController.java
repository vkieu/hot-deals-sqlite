package com.futuretex.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.websocket.server.PathParam;

import com.futuretex.domain.Product;
import com.futuretex.service.ProductServices;
import com.futuretex.service.SendHTMLEmail;
import com.futuretex.utils.ProductHelper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller    // This means that this class is a Controller
@RequestMapping(path = "/product") // This means URL's start with /demo (after Application path)
public class ProductController {
	final static Logger LOG = Logger.getLogger(ProductController.class);

	@Autowired
	private ProductServices productServices;

	@Autowired
	private SendHTMLEmail mailer;

	@PostMapping(path = "/email", consumes = "application/json")
	public @ResponseBody
	String emailProducts(@RequestBody List<String> productIds) {
		LOG.debug("emailProducts called with " + productIds);
		List<Product> products = productServices.getProductsByIds(productIds);
		String html = ProductHelper.productsToHTML(products);
		//sent by email
		mailer.sendHTML(html);
		return "Email Sent!";
	}

	@PostMapping(path = "/markForDelete", consumes = "application/json")
	public @ResponseBody
	String markForDelete(@RequestBody String productId) {
		LOG.debug("markForDelete called with " + productId);
		Optional<Product> opt = productServices.getProductById(productId);
		if(!opt.isPresent()) {
			return "Nok";
		}
		Product product = opt.get();
		product.setMarkedForDelete(Boolean.TRUE);
		product = productServices.createOrUpdate(product);
		return "Ok";
	}

//	@GetMapping(path = "/add") // Map ONLY GET Requests
//	public @ResponseBody
//	String addNewUser(@RequestParam String name, @RequestParam String email) {
//		// @ResponseBody means the returned String is the response, not a view name
//		// @RequestParam means it is a parameter from the GET or POST request
//
//		return "Saved";
//	}

//	@GetMapping(path = "/all")
//	public @ResponseBody Iterable<User> getAllUsers() {
//		// This returns a JSON or XML with the users
//		return userRepository.findAll();
//	}

	@GetMapping(path = "/search")
	public ModelAndView search(
			@RequestParam(required = false) String title,
			@RequestParam(required = false) String id,
			@RequestParam(required = false) String price,
			@RequestParam(required = false) String discount
			) {
		List<Product> products = productServices.findProductsBy(title);
		ModelAndView mav = new ModelAndView("products-list");
		mav.addObject("products", products);
		mav.addObject("title", "Products Listing");
		return mav;
	}

	@GetMapping(path = "/get/{id}")
	public ModelAndView getProduct(@PathVariable("id") String id) {
		List<Product> products = productServices.getProductsByIds(Collections.singletonList(id));
		ModelAndView mav = new ModelAndView("products-list");
		mav.addObject("products", products);
		mav.addObject("title", "Products Listing");
		return mav;
	}
}
