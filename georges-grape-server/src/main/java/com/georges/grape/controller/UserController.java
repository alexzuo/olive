package com.georges.grape.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.georges.grape.data.Event;
import com.georges.grape.data.GrapeUser;
import com.georges.grape.repository.GrapeUserRepository;
import com.mongodb.DB;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

@Controller
@RequestMapping("/api/user")
public class UserController extends BaseGrapeController {

    @Autowired(required = true)
    GrapeUserRepository repository;

    @RequestMapping(method = RequestMethod.GET, value = "/users")
    public @ResponseBody
    List<GrapeUser> getAllUsers(HttpServletRequest request) {
        checkAuthentication(request);

        List<GrapeUser> users = new ArrayList<GrapeUser>();

        Iterator<GrapeUser> p = repository.findAll().iterator();

        while (p.hasNext())
        {
            users.add(p.next());
        }

        return users;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/user")
    public @ResponseBody
    GrapeUser adduser(@RequestBody GrapeUser e) {
        repository.save(e);
        return e;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/me")
    public @ResponseBody
    GrapeUser getMe(HttpServletRequest request) {
        checkAuthentication(request);

        String userId = request.getHeader("userId");
        return repository.findById(userId);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/me")
    public @ResponseBody
    void updateMe(@RequestBody GrapeUser user, HttpServletRequest request) {
        checkAuthentication(request);
        //TODO: should not use passed user directly
        String userId = request.getHeader("userId");
        if (userId.equals(user.getId()))
        {
            GrapeUser newU=repository.save(user);
            System.err.println(newU.toString());
        }
        else
        {

        }

    }

    @RequestMapping(method = RequestMethod.POST, value = "/avatar")
    public @ResponseBody
    String updateProfilePicture(MultipartHttpServletRequest request) {
        checkAuthentication(request);
        String userId = request.getHeader("userId");
        MultipartFile a = request.getFile("file");

        DB db = mongoTemplate.getDb();
        GridFS gfs = new GridFS(db, "profileAvatar");

        GrapeUser user = repository.findById(userId);
        String oldPicUrl = user.getAvatarUri();

        // If the picture is not set, or it is from external, create a new one
        // in MongoDb
        GridFSInputFile in;
        try {
            in = gfs.createFile(a.getBytes());
            in.save();
            String picId = (String) in.getId().toString();
            System.err.println("picId: " + picId.toString());

            Field field = ReflectionUtils.findField(GrapeUser.class, "avatarUri", String.class);
            ReflectionUtils.makeAccessible(field);
            ReflectionUtils.setField(field, user, picId);

            repository.save(user);
            if (oldPicUrl != null && !oldPicUrl.startsWith("http"))
            {
                gfs.remove(new ObjectId(oldPicUrl));
            }
            return user.getAvatarUri();

        } catch (IOException e1) {
            // TODO: return GrapeException
            e1.printStackTrace();

        }
        return null;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/avatar/{id}")
    public @ResponseBody
    void getProfilePicture(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) {
        checkAuthentication(request);

        DB db = mongoTemplate.getDb();
        GridFS gfs = new GridFS(db, "profileAvatar");
        GridFSDBFile file = gfs.find(new ObjectId(id));
        if(file==null)
            return;
        
        response.setContentType("application/octet-stream"); 
        response.setContentLength(new Long(file.getLength()).intValue());
        response.setHeader("Content-Disposition","attachment; filename=file");
 
        try {
            FileCopyUtils.copy(file.getInputStream(), response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
                
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/user/{id}")
    public @ResponseBody
    GrapeUser getUser(@PathVariable String id) {
        return repository.findById(id);
    }
    
}
