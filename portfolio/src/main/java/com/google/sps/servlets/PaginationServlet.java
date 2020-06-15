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
import com.google.sps.data.AllComments;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;

@WebServlet("/pagination")
public final class PaginationServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Entity allCommentsEntity = getAllCommentsEntity();

        Entity userInfoEntity = getUserInfoEntity();

        // Prepare information to be passed as a json
        long total = (long) Optional.ofNullable(allCommentsEntity.getProperty("total")).orElse(0);
        long max = (long) Optional.ofNullable(userInfoEntity.getProperty("max")).orElse(10);
        long page = (long) Optional.ofNullable(userInfoEntity.getProperty("page")).orElse(1);
        String filter = (String) Optional.ofNullable(userInfoEntity.getProperty("filter")).orElse("recent");
        
        // Convert to json.
        String json = "{\"total\": " + total + ", \"max\": " + max + ", \"page\": " + page + ", \"filter\": \"" + filter + "\"}";
        response.setContentType("application/json;");
        response.getWriter().println(json);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Entity userInfoEntity = getUserInfoEntity();

        // Update the page property of UserInfo.
        long newPage = Long.parseLong(Optional.ofNullable(request.getParameter("i")).orElse(null)) + 1;
        userInfoEntity.setProperty("page", newPage);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(userInfoEntity);

        response.sendRedirect("/contact.html");
        return;
    }

    //Converts the comments array  into a JSON string using the Gson library.
    private String convertToJsonUsingGson(List<AllComments> allComments) {
        Gson gson = new Gson();
        String json = gson.toJson(allComments);
        return json;
    }

    // Accesses the datastore to get the AllComments entity. Returns the entity or null if one does not exist.
    private Entity getAllCommentsEntity() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query queryAllComments = new Query("AllComments");
        PreparedQuery resultsAllComments = datastore.prepare(queryAllComments);

        // Return null if there are no AllComments entity.
        if (resultsAllComments.countEntities() == 0) {
            return null;
        }

        Iterator<Entity> iterAllComments = resultsAllComments.asIterator();
        Entity allCommentsEntity = iterAllComments.next(); 

        return allCommentsEntity;
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

        } catch (Exception e) {
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
}
