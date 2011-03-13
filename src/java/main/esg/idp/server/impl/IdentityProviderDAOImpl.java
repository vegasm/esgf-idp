package esg.idp.server.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import esg.idp.server.api.Identity;
import esg.idp.server.api.IdentityProvider;
import esg.node.security.UserInfo;
import esg.node.security.UserInfoDAO;

/**
 * Implementation of {@link IdentityProvider} that is backed up by a relational database.
 * @author Luca Cinquini
 *
 */
@Service("identityProvider")
public class IdentityProviderDAOImpl implements IdentityProvider {
	
	/**
	 * Database access class
	 */
	private UserInfoDAO userInfoDAO = null;
	
	private final Log LOG = LogFactory.getLog(this.getClass());
	
	//@Autowired
	public IdentityProviderDAOImpl(final @Qualifier("dbProperties") Properties props) {
		this.userInfoDAO = new UserInfoDAO(props);
	}
	
	public IdentityProviderDAOImpl() throws IOException, FileNotFoundException {
	    
	    final Properties props = new Properties();
	    File propertyFile = new File( System.getenv().get("ESGF_HOME")+"/config/esgf.properties" );
	    if (!propertyFile.exists()) propertyFile = new File("/esg/config/esgf.properties");
	    props.load( new FileInputStream(propertyFile) );
	    if (LOG.isInfoEnabled()) LOG.info("Loading properties from file: "+propertyFile.getAbsolutePath());
        this.userInfoDAO = new UserInfoDAO(props);
        
    }

	@Override
	public boolean authenticate(String openid, String password) {
		
		return userInfoDAO.checkPassword(openid, password);
		
	}

	@Override
	public Identity getIdentity(String openid) {
		
		final UserInfo user = userInfoDAO.getUserById(openid);
		if (user.isValid()) {
			return new IdentityImpl(openid, user.getUserName());
		} else {
			return null;
		}
		
	}

}
