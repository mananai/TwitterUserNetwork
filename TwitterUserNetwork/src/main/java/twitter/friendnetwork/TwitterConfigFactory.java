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
	
}
