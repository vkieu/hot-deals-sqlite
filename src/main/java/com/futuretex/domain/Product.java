package com.futuretex.domain;

import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import javax.validation.constraints.AssertTrue;

import org.springframework.util.Assert;

@Entity
public class Product {

	@Id
	private String id;
	private String title;
	private String description;

	private Long createTs;
	private Long updateTs;

	private BigDecimal price;
	private String priceString;

	private Double percentageDiscount;
	private String discountString;

	private String detailedLink;
	private String dealLink;
	private String merchantLink;
	private String imageLink;

	private String voteString;
	private Integer temperature;

	private Boolean expired = Boolean.FALSE;
	private Boolean markedForDelete = Boolean.FALSE;

	private Long dateFound;
	private String expiredDate;

	private Integer numberOfComments;

	@Transient
	boolean isNew;

	public Long getCreateTs() {
		return createTs;
	}

	public Long getUpdateTs() {
		return updateTs;
	}

	@PrePersist
	public void prePersist() {
		this.createTs = System.currentTimeMillis();
		this.updateTs = this.createTs;
		this.isNew = true;
	}

	@PreUpdate
	public void preUpdate() {
		this.updateTs = System.currentTimeMillis();
		this.isNew = false;
	}

	public String getDiscountString() {
		return discountString;
	}

	public void setDiscountString(String discountString) {
		this.discountString = discountString;
	}

	public Boolean getMarkedForDelete() {
		return markedForDelete;
	}

	public void setMarkedForDelete(Boolean markedForDelete) {
		this.markedForDelete = markedForDelete;
	}

	public boolean isNew() {
		return isNew;
	}

	public String getDetailedLink() {
		return detailedLink;
	}

	public void setDetailedLink(String detailedLink) {
		this.detailedLink = detailedLink;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getPriceString() {
		return priceString;
	}

	public void setPriceString(String priceString) {
		this.priceString = priceString;
	}

	public Double getPercentageDiscount() {
		return percentageDiscount;
	}

	public void setPercentageDiscount(Double percentageDiscount) {
		this.percentageDiscount = percentageDiscount;
	}

	public String getDealLink() {
		return dealLink;
	}

	public void setDealLink(String dealLink) {
		this.dealLink = dealLink;
	}

	public String getMerchantLink() {
		return merchantLink;
	}

	public void setMerchantLink(String merchantLink) {
		this.merchantLink = merchantLink;
	}

	public String getImageLink() {
		return imageLink;
	}

	public void setImageLink(String imageLink) {
		this.imageLink = imageLink;
	}

	public String getVoteString() {
		return voteString;
	}

	public void setVoteString(String voteString) {
		this.voteString = voteString;
	}

	public Integer getTemperature() {
		return temperature;
	}

	public void setTemperature(Integer temperature) {
		this.temperature = temperature;
	}

	public Boolean getExpired() {
		return expired;
	}

	public void setExpired(Boolean expired) {
		this.expired = expired;
	}

	public String getExpiredDate() {
		return expiredDate;
	}

	public void setExpiredDate(String expiredDate) {
		this.expiredDate = expiredDate;
	}

	public Long getDateFound() {
		return dateFound;
	}

	public void setDateFound(Long dateFound) {
		this.dateFound = dateFound;
	}

	public Integer getNumberOfComments() {
		return numberOfComments;
	}

	public void setNumberOfComments(Integer numberOfComments) {
		this.numberOfComments = numberOfComments;
	}

	public Product() {
	}

	public Product(String id, String title) {
		this.id = id;
		this.title = title;
	}

	public Product merge(Product p) {
		Assert.isTrue(id.equals(p.id));
		this.createTs = p.createTs;
		this.markedForDelete = p.markedForDelete;
		return this;
	}

	public String getId() {
		return this.id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Product)) return false;
		Product product = (Product) o;
		return new org.apache.commons.lang3.builder.EqualsBuilder()
				.append(title, product.title)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new org.apache.commons.lang3.builder.HashCodeBuilder(17, 37)
				.append(title)
				.toHashCode();
	}

	@Override
	public String toString() {
		return "Product{" +
				" detailedLink=" + detailedLink +
				", id='" + id + '\'' +
				", title='" + title + '\'' +
				", price=" + price +
				", priceString=" + priceString +
				", voteString='" + voteString + '\'' +
				", expired=" + expired +
				", merchant=" + merchantLink +
				", marked for delete=" + markedForDelete +
				'}';
	}
}
