package twitter.friendnetwork;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import twitter4j.conf.ConfigurationBuilder;

public class TwitterConfigFactory {
	public static ConfigurationBuilder[] create(String[] propertiesFileNames) throws IOException {
		System.out.printf("Loading %d twitter properties files...", propertiesFileNames.length);
		ConfigurationBuilder[] configs = new ConfigurationBuilder[propertiesFileNames.length];
		
		for (int i = 0 ; i < configs.length ; i++) {
			try (InputStream input = new FileInputStream(propertiesFileNames[i])) {
	            Properties props = new Properties();
	            props.load(input);
				configs[i] = new ConfigurationBuilder();
	    		configs[i].setOAuthConsumerKey(props.getProperty("oauth.consumerKey"));
	    		configs[i].setOAuthConsumerSecret(props.getProperty("oauth.consumerSecret"));
	    		configs[i].setOAuthAccessToken(props.getProperty("oauth.accessToken"));
	    		configs[i].setOAuthAccessTokenSecret(props.getProperty("oauth.accessTokenSecret"));
	        }
		}
		System.out.println("Done");
		return configs;
	}
	
	@Deprecated
	public static ConfigurationBuilder[] create() {
		ConfigurationBuilder[] configs = new ConfigurationBuilder[4];
		
		configs[0] = new ConfigurationBuilder();
		configs[0].setOAuthConsumerKey("G1WLX6qGwMIqNFvhtDh2TtoFa");
		configs[0].setOAuthConsumerSecret("rMTa28YDDLJByAhtIywCxg0W5lDbWEdac880V1nPiey69PEyQs");
		configs[0].setOAuthAccessToken("193277816-dvqzbkvinvGWkvHF0ndj4LYqoaHRB1QyopF2MV0a");
		configs[0].setOAuthAccessTokenSecret("nfCEnJbYuKkyiq2bbhVllhnZbtWA9lJr8QypmZ7VAHQGi");

		configs[1] = new ConfigurationBuilder();
		configs[1].setOAuthConsumerKey("Upz8OPJWJrKevifdBAHdR2lrV");
		configs[1].setOAuthConsumerSecret("9Ooh9QCKl1yHc5yDmgJP4iaTbfbDWHi5m5Weum2hFp4WwjLxjD");
		configs[1].setOAuthAccessToken("193277816-BFrjWyaSNBWPb1mdCbR3sJw6JANF0UsT2JA3Li9f");
		configs[1].setOAuthAccessTokenSecret("GrVnhaRvWpCqMyjJzURYY5XTE7NPmPGTReABBJ9XVhaQD");

		
		configs[2] = new ConfigurationBuilder();
		configs[2].setOAuthConsumerKey("vlSSiu3Rf2qaWnt0h5t6Algtt");
		configs[2].setOAuthConsumerSecret("cBwZIMld2En6GCDK46kQK3S5bm1BynCDuyhmwMyrgHGWksIeRv");
		configs[2].setOAuthAccessToken("193277816-WLlXcu7qXBShtkIgynXsmxmS6zvdbfRndTPcHzcp");
		configs[2].setOAuthAccessTokenSecret("o9cJ2R2buX23TbWHnghOO56VhGnyspYLIowPEI6GdmrWu");

		configs[3] = new ConfigurationBuilder();
		configs[3].setOAuthConsumerKey("Xwj52mLhyo6oErzblTNzoKndg");
		configs[3].setOAuthConsumerSecret("I7i4nB2ij8umo00RHuQFHGTT6C3z9uT2Mc69G5VoHqGxFyKyfb");
		configs[3].setOAuthAccessToken("193277816-65NjG9yiEO2CtiZyCN0SAtFhfwIBMFAcVLjtWYYq");
		configs[3].setOAuthAccessTokenSecret("RSZSzmTJYGgNpNMV205rWJ1wdHCaGaHnYH3ChJqVGB7Bl");

		
		return configs;
	}
}
