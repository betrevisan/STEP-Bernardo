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

@WebServlet("/translate")
public final class LanguageServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get the language input from the form.
        String language = Optional.ofNullable(request.getParameter("search-by")).orElse("en");

        Entity userInfoEntity = getUserInfoEntity();

        // Update the language property
        userInfoEntity.setProperty("language", language);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        // Add the updated entity back in the datastore
        datastore.put(userInfoEntity);

        response.sendRedirect("/contact.html");
        return;
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
            defaultEntity.setProperty("max", 10);
            defaultEntity.setProperty("page", 1);
            defaultEntity.setProperty("language", "en");
            defaultEntity.setProperty("filter", "recent");
            defaultEntity.setProperty("searchBy", "name");
            return defaultEntity;
        }
    }
}
