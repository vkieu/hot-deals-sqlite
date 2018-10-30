package com.futuretex.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.futuretex.domain.Product;
import com.futuretex.repository.IProductRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductServices {

	final static Logger LOG = Logger.getLogger(ProductServices.class);

	@Autowired
	private IProductRepository productRepository;

	private final Pattern PAGE_PATTERN = Pattern.compile(".*page=(\\d+).*");

	private Queue<Document> queue = new ConcurrentLinkedQueue<>();

	public List<Product> getProductsByIds(List<String> productIds) {
		List<Product> returnList = new ArrayList<>();
		Iterable<Product> products = productRepository.findAllById(productIds.stream().collect(Collectors.toList()));
		products.forEach(returnList::add);
		return returnList;
	}

	public Optional<Product> getProductById(String productId) {
		return productRepository.findById(productId);
	}

	public Product createOrUpdate(Product product) {
		return productRepository.save(product);
	}

	public List<Product> findProductsBy(String title) {
		return productRepository.searchByTitle(title);
	}

	private void startEngine(ProductFilter filter) {
		Runnable task = () -> {
			while (true) {
				LOG.debug("queue size? " + queue.size());
				Document doc = queue.poll();
				if (doc != null) {
					long start = System.currentTimeMillis();
					Elements items = doc.select("article.thread--deal");
					LOG.debug("Found " + items.size() + " raw result(s)");

					for (Element item : items) {
						Element element = item.getElementsByClass("thread--type-list").first();
						if (element != null) {
							try {
								Product product = ProductParser.parseListElement(item);
								Optional<Product> optional = productRepository.findById(product.getId());
								if (optional.isPresent()) {
									product.merge(optional.get());
								}
								if (filter.apply(product)) {
									product = productRepository.save(product);
									if (product.isNew()) {
										LOG.info("NEW->" + product);
									}
								} else {
									if (optional.isPresent()) {
										productRepository.deleteById(product.getId());
										LOG.info("DELETED->" + product);
									}
								}
							} catch (Exception e) {
								LOG.error("Error processing " + element, e);
							}
						}
					}
					LOG.debug("Page proccessed in " + (System.currentTimeMillis() - start) + " ms");
				} else {
					try {
						Thread.sleep(500L);
					} catch (InterruptedException e) {
						//noop
					}
				}
			}
		};
		Thread t = new Thread(task);
		t.start();
	}

	private ExecutorService executor = Executors.newFixedThreadPool(10);

	private void download(final String location) throws Exception {
		Runnable task = () -> {
			while(true) {
				long start = System.currentTimeMillis();
				try {
					Document doc = Jsoup.connect(location).ignoreContentType(true).get();
					queue.add(doc);
					LOG.debug(doc.title());
					LOG.info(location + " - fetch html: " + (System.currentTimeMillis() - start) + " ms");
					return;
				} catch (Exception ex) {
					LOG.error("socketException, wait and retrying", ex);
					try {
						Thread.sleep(30 * 1000L);//waits 30 seconds
					} catch (InterruptedException ie) {
						//noop
					}
				}
			}
		};
		executor.submit(task);
	}

	private void fetchData(String site, int stopPage) throws Exception {
		int page = findPageFromUrl(MarkerUtility.getSiteMarker(site));

		while (true) {
			String location = site + ((site.indexOf("?") > 0) ? ("&page=") : "?page=") + page;
			Document doc;
			long start = System.currentTimeMillis();
			try {
				LOG.info(location);
				doc = Jsoup.connect(location).ignoreContentType(true).ignoreHttpErrors(true)
						.cookie(" ",
								"__cfduid=d5da5d6947ed201036899d013036b537c1539020192; f_v=%223cce0f90-d82f-11e8-9c9f-0242ed85b783%22; view_layout_horizontal=%221-1%22; show_my_tab=0; _vwo_uuid_v2=D813E03448982BB7A0DC8576B00B15ADB|ab22983c4ed892e1f4323d2a00417a2d; _ga=GA1.2.1323150914.1540455805; _vwo_uuid=D813E03448982BB7A0DC8576B00B15ADB; browser_push_permission_requested=1540455844; cf_clearance=89b86197c6d2ea27b17b805c6208e2ccfca52612-1540852092-3600-250; pepper_session=%22e8f277b5de50af88f0341f2feed18eb1befffdf8%22; _vis_opt_s=11%7C; _vis_opt_test_cookie=1; _gid=GA1.2.1251257834.1540852094; _gat=1; navi=%7B%22homepage%22%3A%22hot%22%7D; _vwo_ds=3%3Aa_0%2Ct_0%3A0%241540455803%3A66.27484373%3A%3A%3A150_0%2C149_0%2C136_0%2C135_0%2C70_0%3A0").get();
				queue.add(doc);
				LOG.debug(doc.title());
			} catch (java.net.SocketTimeoutException socketException) {
				LOG.error("socketException, wait and retrying");
				Thread.sleep(30 * 1000L);//waits 30 seconds
				continue;
			}
			long downloaded = System.currentTimeMillis() - start;

			for(int i = 0; i<10; i++) {
				location = site + ((site.indexOf("?") > 0) ? ("&page=") : "?page=") + (++page);
				download(location);
			}

			//mark last known location
			MarkerUtility.addMarker(site, location);
			LOG.info(location + " - fetch html: " + downloaded + " ms");

			if (!hasNextPage(doc) || (stopPage != -1 && page >= stopPage)) {
				LOG.info("End of page reached " + page);
				break;
			} else {
				page++;
			}

			//dont want to DOS the server
			pause();
		}

		MarkerUtility.clearMarker(site);
	}


	public void captureProductList(final String site, int stopPage) throws Exception {

		if (!MarkerUtility.hasMarker(site) && MarkerUtility.hasOtherMarkers()) {
			return;
		}

		ProductFilter filter = new ProductFilter();

		startEngine(filter);

		fetchData(site, stopPage);

		LOG.info(filter.getSummary());
	}

	private boolean hasNextPage(Document doc) {
		return !doc.select("a.pagination-next").isEmpty();
	}

	private int findPageFromUrl(String site) {
		Matcher matcher = PAGE_PATTERN.matcher(site);
		if (matcher.find()) {
			String pageString = matcher.group(1);
			if (!StringUtils.isEmpty(pageString)) {
				return Integer.parseInt(pageString);
			}
		}
		return 1;
	}


	private void pause() {
//		try {
//			//don't want to DOS the server
//			Thread.sleep(100L);
//		} catch (InterruptedException ie) {
//			//NOOP
//		}
	}
}
