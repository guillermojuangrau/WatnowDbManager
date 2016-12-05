import java.math.BigDecimal;
import java.sql.Date;

public class VenueBean {

	public String establishment;
	public String tDescription;
	public String address;
	public String postalCode;
	public String city;
	public int iCityId;
	public String country;
	public String venueType;
	

	public BigDecimal latitude;
	public BigDecimal longitude;
	public String phonenumber;
	public int priceRange;
	public int likes;
	public int dislikes;
	public java.sql.Date dInsertedDateTime;
	public java.sql.Date dUpdatedDateTime;
	public int insertedBy;
	public int updatedBy;
	
	public VenueBean(String establishment, String address, String postalCode, String city, int iCityId, String country, String venueType,
			BigDecimal latitude, BigDecimal longitude, String phonenumber, int priceRange, Date dInsertedDateTime,
			Date dUpdatedDateTime) {
		
		this.establishment = establishment;
		tDescription = "";
		this.address = address;
		this.postalCode = postalCode;
		this.city = city;
		country = "1";
		this.venueType = venueType;
		this.iCityId = iCityId;
		this.latitude = latitude;
		this.longitude = longitude;
		this.phonenumber = phonenumber;
		this.priceRange = priceRange;
		likes = 0;
		dislikes = 0;
		this.dInsertedDateTime = dInsertedDateTime;
		this.dUpdatedDateTime = dUpdatedDateTime;
		insertedBy = 0;
		updatedBy = 0;
	}

	public String getEstablishment() {
		return establishment;
	}

	public void setEstablishment(String establishment) {
		this.establishment = establishment;
	}

	public String gettDescription() {
		return tDescription;
	}

	public void settDescription(String tDescription) {
		this.tDescription = tDescription;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public int getiCityId() {
		return iCityId;
	}

	public void setiCityId(int iCityId) {
		this.iCityId = iCityId;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
	public String getVenueType() {
		return venueType;
	}

	public void setVenueType(String venueType) {
		this.venueType = venueType;
	}
	

	public BigDecimal getLatitude() {
		return latitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}

	public String getPhonenumber() {
		return phonenumber;
	}

	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}

	public int getPriceRange() {
		return priceRange;
	}

	public void setPriceRange(int priceRange) {
		this.priceRange = priceRange;
	}

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public int getDislikes() {
		return dislikes;
	}

	public void setDislikes(int dislikes) {
		this.dislikes = dislikes;
	}

	public java.sql.Date getdInsertedDateTime() {
		return dInsertedDateTime;
	}

	public void setdInsertedDateTime(java.sql.Date dInsertedDateTime) {
		this.dInsertedDateTime = dInsertedDateTime;
	}

	public java.sql.Date getdUpdatedDateTime() {
		return dUpdatedDateTime;
	}

	public void setdUpdatedDateTime(java.sql.Date dUpdatedDateTime) {
		this.dUpdatedDateTime = dUpdatedDateTime;
	}

	public int getInsertedBy() {
		return insertedBy;
	}

	public void setInsertedBy(int insertedBy) {
		this.insertedBy = insertedBy;
	}

	public int getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(int updatedBy) {
		this.updatedBy = updatedBy;
	}
	
	
	
	
}
