// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Optional;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.sps.data.Comment;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.appengine.api.datastore.FetchOptions;


@WebServlet("/data")
public final class DataServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get the information of the currently logged in user.
        Entity userInfoEntity = getUserInfoEntity();
        
        String selectedFilter = (String) userInfoEntity.getProperty("filter");

        Query queryComments = null;
        // Assign the correct query to queryComments according to the filter settings in place. 
        switch (selectedFilter) {
            case "recent":
                queryComments = new Query("Comment").addSort("time", SortDirection.DESCENDING);
                break;
            case "oldest":
                queryComments = new Query("Comment").addSort("time", SortDirection.ASCENDING);
                break;
            case "top":
                queryComments = new Query("Comment").addSort("popularity", SortDirection.DESCENDING);
                break;
            case "bottom":
                queryComments = new Query("Comment").addSort("popularity", SortDirection.ASCENDING);
                break;
            default:
                queryComments = new Query("Comment").addSort("name", SortDirection.ASCENDING);
                break;
        }
 
        // Add the appropriate searchBy filter.
        String searchBy = (String) userInfoEntity.getProperty("searchBy");
        String searchInput = (String) userInfoEntity.getProperty("searchInput");
        if (searchInput != null) {
            Filter searchFilter = null;
            if (searchBy.equals("username")) {
                searchFilter = new FilterPredicate("username", FilterOperator.EQUAL, searchInput);
            } else {
                searchFilter = new FilterPredicate("name", FilterOperator.EQUAL, searchInput);
            }
            queryComments = queryComments.setFilter(searchFilter);
        }
        
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery resultsComments = datastore.prepare(queryComments);
        List<Comment> comments = iterateQuery(resultsComments);
        String json = convertToJsonUsingGson(comments);
        response.setContentType("application/json;");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().println(json);
    }

    // Converts the comments array  into a JSON string using the Gson library.
    private String convertToJsonUsingGson(List<Comment> comments) {
        Gson gson = new Gson();
        String json = gson.toJson(comments);
        return json;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get the information of the currently logged in user.
        Entity userInfoEntity = getUserInfoEntity();
        String name = (String) userInfoEntity.getProperty("name");
        String username = (String) userInfoEntity.getProperty("username");

        // If the user opted to post the comment anonymously, make the name Anonymous.
        String anonymous = Optional.ofNullable(request.getParameter("anonymous")).orElse("off");
        if (anonymous.equals("on")) {
            name = "Anonymous";
            username = "anonymous";
        }
        
        String comment = Optional.ofNullable(request.getParameter("user-comment")).orElse("error");

        // Get current user's email.
        UserService userService = UserServiceFactory.getUserService();
        String email = userService.getCurrentUser().getEmail();

        // Add comment to the datastore.
        createComment(comment, name, email, username);

        response.sendRedirect("/contact.html");
        return;
    }

    // Iterates over a comments query and returns an array of comments.
    private List<Comment> iterateQuery(PreparedQuery results) {
        Entity userInfoEntity = getUserInfoEntity();

        long totalComments = getTotal();
        long page = (long) userInfoEntity.getProperty("page");
        long maxComments = (long) userInfoEntity.getProperty("max");

        String language = (String) userInfoEntity.getProperty("language");
        if (language == null) {
            language = "en";
        }

        List<Comment> comments = new ArrayList<>();
        Iterator<Entity> iter = results.asIterator();

        // Iterates over the results until the results are less than the limit on comments or until the end of all results.
        for (int count = 0; count < (maxComments * page) && count < totalComments; count++) {
            if (iter.hasNext()) {
                Entity entity = iter.next();

                // Only add comment when it is part of the page the user is currently in.
                if (count >= (maxComments * (page - 1))) {
                    Comment comment = new Comment(entity);

                    comment.translateComment(language);

                    comments.add(comment);
                }
            }
        }

        return comments;
    }

    // Creates a Comment entity and stores it in the datastore.
    private void createComment(String comment, String name, String email, String username) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity commentEntity = new Entity("Comment");
        commentEntity.setProperty("content", comment);
        long timestamp = System.currentTimeMillis();
        commentEntity.setProperty("time", timestamp);
        commentEntity.setProperty("thumbsup", 0);
        commentEntity.setProperty("thumbsdown", 0);
        commentEntity.setProperty("popularity", 0);
        commentEntity.setProperty("name", name);
        commentEntity.setProperty("email", email);
        commentEntity.setProperty("username", username);
        datastore.put(commentEntity);
    }

    // Returns the username that corresponds to the id that was given or null if there is no username linked to that id.
    private String getUsername(String id) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Filter queryFilter = new FilterPredicate("id", Query.FilterOperator.EQUAL, id);
        Query query = new Query("UserInfo").setFilter(queryFilter);
        PreparedQuery results = datastore.prepare(query); 
        Entity entity = results.asSingleEntity(); 

        if (entity == null) {
            return null;
        }

        String username = (String) entity.getProperty("username");
        return username;     
    }

    // Accesses the datastore to get the UserInfo entity. Returns the entity or null if one does not exist.
    private Entity getUserInfoEntity() {
        try {
            UserService userService = UserServiceFactory.getUserService();
            String id = userService.getCurrentUser().getUserId();
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            Filter queryFilter = new FilterPredicate("id", Query.FilterOperator.EQUAL, id);
            Query query = new Query("UserInfo").setFilter(queryFilter);
            PreparedQuery results = datastore.prepare(query); 
            Entity userInfoEntity = results.asSingleEntity(); 

            return userInfoEntity;

        } catch (NullPointerException e) {
            // If the user is not logged in, return default user entity
            Entity defaultEntity = new Entity("UserInfo");
            defaultEntity.setProperty("max", (long) 10);
            defaultEntity.setProperty("page", (long) 1);
            defaultEntity.setProperty("language", "en");
            defaultEntity.setProperty("filter", "recent");
            defaultEntity.setProperty("searchBy", "name");
            return defaultEntity;
        }
    }

    private long getTotal() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query query = new Query("Comment");
        return datastore.prepare(query).countEntities(FetchOptions.Builder.withDefaults());
    }
}
