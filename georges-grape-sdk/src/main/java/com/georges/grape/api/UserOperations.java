
package com.georges.grape.api;

import java.io.OutputStream;
import com.georges.grape.data.GrapeUser;

/**
 * User interface of Grape 
 * @author Alex Zuo
 * 
 * Usage sample:
 * <pre>
 *      //get current user's profile
 *      GrapeUser user=rest.userOperations().getMyProfile();
 *      
 *      //update current user's avatar picture
 *       String uri=rest.userOperations().updateMyAvatar("c:\\log.png");
 *       System.err.println("uri: " + uri);
 *       
 *       //update current user's profile
 *       user.setAge(29);
 *       user.setDisplayName("Wayne Wu");
 *       rest.userOperations().updateMyProfile(user);
 *       
 *       //get user's avatar picture
 *       FileOutputStream file;
 *       file = new FileOutputStream("1.png");
 *       rest.userOperations().getAvatar(uri, file);
 *      
 * </pre>
 *
 */

public interface UserOperations {

    /**
     * get the current user's profile
     * @return a {@link GrapeUser} object
     * @throws GrapeException if there is an error while communicating with Grape.
     */
	GrapeUser getMyProfile();
    /**
     * get  the profile of a specified user
     * @param userId the specified user's Id
     * @return a {@link GrapeUser} object
     * @throws GrapeException if there is an error while communicating with Grape.
     */
	GrapeUser getUserProfile(String userId);
	
    /**
     * update the current user's profile, user's avatar picture and location won't be updated
     * 
     * @param a {@link GrapeUser} object, its Id must be already set
     *
     * @throws GrapeException if there is an error while communicating with Grape.
     */
	void updateMyProfile(GrapeUser user);
	
    /**
     * update the current user's avatar by uploading a new picture
     * 
     * @param pictureFilePath the local file path of the avatar picture
     * @return the avatar's URI, actually it is an Id
     * @throws GrapeException if there is an error while communicating with Grape.
     */
	String updateMyAvatar(String pictureFilePath);
	
    /**
     * get the current user's avatar picture 
     * 
     * @param  avatarUri the avatar's URI, it can be a full HTTP URL or just an Id. 
     *         When you want to get an user's avatar, just pass the "avatarUri" saved in {@link GrapeUser}
     * @param  output the output stream which this API will write binary data of the picture to
     * @throws GrapeException if there is an error while communicating with Grape.
     */
	void getAvatar(String avatarUri, OutputStream output);
	
}
