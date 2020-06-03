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
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.sps.data.Comment;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;


/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public final class DataServlet extends HttpServlet {

    private int maxComments = 10;
    private Key allKey;

    @Override
    public void init() {
        // Query the AllComments entity
        Query query = new Query("AllComments");
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);

        // Only creates a new AllComments entity if one has not yet been created
        if (results.countEntities() == 0) {
            createAllComments();
        } else {
            // If there is already an entity in the datastore, simply store its key
            Iterator<Entity> iter = results.asIterator();
            Entity entity = iter.next();
            allKey = entity.getKey();
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Create a query instance
        Query query = new Query("Comment").addSort("time", SortDirection.DESCENDING);

        // Instantiate the datastore
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        // Get prepared instance of the query
        PreparedQuery results = datastore.prepare(query);

        // Iterate over results
        List<Comment> comments = iterateQuery(results);
        
        // Convert to json
        String json = convertToJsonUsingGson(comments);
        response.setContentType("application/json;");
        response.getWriter().println(json);
    }

    /**
    * Converts the comments array  into a JSON string using the Gson library. Note: We first added
    * the Gson library dependency to pom.xml.
    */
    private String convertToJsonUsingGson(List<Comment> comments) {
        Gson gson = new Gson();
        String json = gson.toJson(comments);
        return json;
    }


    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        // Get the input from the form.
        String max = getParameter(request, "max-comments", null);
        // If a maximum number of comments has been selected, only update the maxComments variable and return.
        if (max != null) {

            int tempMax;

            try {
                tempMax = Integer.parseInt(max);
            } catch (NumberFormatException e) {
                // Return if max was not numeric
                response.sendRedirect("/contact.html");
                return;
            }

            // Only update maxComments if tempMax was not negative
            if (tempMax > 0)
            {
                maxComments = tempMax;
            }

            response.sendRedirect("/contact.html");
            return;
        }
        
        // Get the input from the form.
        String comment = getParameter(request, "user-comment", null);

        // Return error message if the user did not input any comment.
        if (comment == null) {
            response.setContentType("text/html;");
            response.getWriter().println("Enter a comment before submitting.");
            return;
        }
    
        // If the user did submit a comment, add it to the datastore
        createComment(comment);

        // Increase total of all comments by 1
        changeAllCommentsTotal(1);

        // Respond with a success message
        response.setContentType("text/html;");
        response.getWriter().println("Your comment has been registered. Thank you!");
    }

    /** Returns the desired parameter entered by the user, or null if the user input was invalid. */
    private String getParameter(HttpServletRequest request, String name, String defaultValue) {
        // Get the input from the form.
        String value = request.getParameter(name);

        if (value == null) {
            return defaultValue;
        }

        return value;
    }

    // Iterates over a comments query and returns an array of comments
    private List<Comment> iterateQuery(PreparedQuery results) {
        List<Comment> comments = new ArrayList<>();
        Iterator<Entity> iter = results.asIterator();
        int totalComments = results.countEntities();

        for (int count = 0; count < maxComments && count < totalComments; count++) {
            Entity entity = iter.next();

            long id = entity.getKey().getId();
            String content = (String) entity.getProperty("content");
            long time = (long) entity.getProperty("time");
            long thumbsup = (long) entity.getProperty("thumbsup");
            long thumbsdown = (long) entity.getProperty("thumbsdown");

            Comment comment = new Comment(id, content, time, thumbsup, thumbsdown);
            comments.add(comment);
        }

        return comments;
    }

    // Creates a Comment entity and stores it in the datastore
    private void createComment(String comment) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity commentEntity = new Entity("Comment");
        commentEntity.setProperty("content", comment);
        long timestamp = System.currentTimeMillis();
        commentEntity.setProperty("time", timestamp);
        commentEntity.setProperty("thumbsup", 0);
        commentEntity.setProperty("thumbsdown", 0);
        datastore.put(commentEntity);
    }

    // Creates an AllComments entity and stores it in the datastore
    private void createAllComments() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity allComments = new Entity("AllComments");
        allComments.setProperty("total", 0);
        allComments.setProperty("max", maxComments);
        allComments.setProperty("page", 1);
        datastore.put(allComments);

        // Stores the key to the entity that stores information about all comments
        allKey = allComments.getKey();
    }

    // Changes the value of the total property in AllComments and updates the datastore
    private void changeAllCommentsTotal(int value) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        // Get the all comments entity using its key
        Entity allEntity;
        try {
            allEntity = datastore.get(allKey);
        } catch(Exception e) {
            return;
        }

        long prevTotal = (long) allEntity.getProperty("total");
        long newTotal = prevTotal + value;

        allEntity.setProperty("total", newTotal);
        datastore.put(allEntity);
    }
}
