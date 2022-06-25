package entities;

import java.util.Date;

import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.User;

public class UserImp implements User {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3650394847385913786L;
	private long id;
	private String name;
	private String screenName;
	private String description;
	private int favoritesCount;
	private int followersCount;
	private int friendsCount;
	private int statusesCount;
	private String lang;
	private String location;
	private String url;
	private boolean isProtected;
	private boolean isVerified;
	
	public UserImp(long id, String name, String screenName, String description, int favoritesCount, int followersCount,
			int friendsCount, int statusesCount, String lang, String location, String url, boolean isProtected,
			boolean isVerified) {
		super();
		this.id = id;
		this.name = name;
		this.screenName = screenName;
		this.description = description;
		this.favoritesCount = favoritesCount;
		this.followersCount = followersCount;
		this.friendsCount = friendsCount;
		this.statusesCount = statusesCount;
		this.lang = lang;
		this.location = location;
		this.url = url;
		this.isProtected = isProtected;
		this.isVerified = isVerified;
	}



	public UserImp(long id, String name, String screenName, int favoritesCount, int followersCount, int friendsCount,
			int statusesCount, String url, boolean isProtected, boolean isVerified) {
		super();
		this.id = id;
		this.name = name;
		this.screenName = screenName;
		this.favoritesCount = favoritesCount;
		this.followersCount = followersCount;
		this.friendsCount = friendsCount;
		this.statusesCount = statusesCount;
		this.url = url;
		this.isProtected = isProtected;
		this.isVerified = isVerified;
	}
	
	

	@Override
	public String toString() {
		return "User[id=" + id + ", name=" + name + ", screenName=" + screenName + "]";
	}



	@Override
	public int compareTo(User o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getAccessLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public RateLimitStatus getRateLimitStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String get400x400ProfileImageURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String get400x400ProfileImageURLHttps() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getBiggerProfileImageURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getBiggerProfileImageURLHttps() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getCreatedAt() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public URLEntity[] getDescriptionURLEntities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEmail() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getFavouritesCount() {
		return this.favoritesCount;
	}

	@Override
	public int getFollowersCount() {
		return this.followersCount;
	}

	@Override
	public int getFriendsCount() {
		return this.friendsCount;
	}

	@Override
	public long getId() {
		return this.id;
	}

	@Override
	public String getLang() {
		return this.lang;
	}

	@Override
	public int getListedCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getLocation() {
		return this.location;
	}

	@Override
	public String getMiniProfileImageURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMiniProfileImageURLHttps() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getOriginalProfileImageURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOriginalProfileImageURLHttps() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProfileBackgroundColor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProfileBackgroundImageURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProfileBackgroundImageUrlHttps() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProfileBanner1500x500URL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProfileBanner300x100URL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProfileBanner600x200URL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProfileBannerIPadRetinaURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProfileBannerIPadURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProfileBannerMobileRetinaURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProfileBannerMobileURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProfileBannerRetinaURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProfileBannerURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProfileImageURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProfileImageURLHttps() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProfileLinkColor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProfileSidebarBorderColor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProfileSidebarFillColor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProfileTextColor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getScreenName() {
		return this.screenName;
	}

	@Override
	public Status getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getStatusesCount() {
		return this.statusesCount;
	}

	@Override
	public String getTimeZone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getURL() {
		// TODO Auto-generated method stub
		return this.url;
	}

	@Override
	public URLEntity getURLEntity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getUtcOffset() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String[] getWithheldInCountries() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isContributorsEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDefaultProfile() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDefaultProfileImage() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFollowRequestSent() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isGeoEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isProfileBackgroundTiled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isProfileUseBackgroundImage() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isProtected() {
		return this.isProtected;
	}

	@Override
	public boolean isShowAllInlineMedia() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isTranslator() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isVerified() {
		return this.isVerified;
	}

}
