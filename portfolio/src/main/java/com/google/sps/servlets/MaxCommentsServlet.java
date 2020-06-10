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
import com.google.sps.data.AllComments;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;

@WebServlet("/max-comments")
public final class MaxCommentsServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get the max-comments input from the form.
        String max = getParameter(request, "max-comments", null);

        // If a maximum number of comments has been selected, only update the maxComments variable and return.
        if (max != null) {
            long tempMax;

            try {
                tempMax = Long.parseLong(max);
            } catch (NumberFormatException e) {
                // Return if max was not numeric.
                response.sendRedirect("/contact.html");
                return;
            }

            // Only update maxComments if tempMax was not negative.
            if (tempMax > 0)
            {
                changeUserInfoMax(tempMax);
            }

            response.sendRedirect("/contact.html");
            return;
        }

        response.sendRedirect("/contact.html");
        return;
    }

    // Returns the desired parameter entered by the user, or null if the user input was invalid.
    private String getParameter(HttpServletRequest request, String name, String defaultValue) {
        // Get the input from the form.
        String value = request.getParameter(name);

        if (value == null) {
            return defaultValue;
        }

        return value;
    }

    // Accesses the datastore to get the UserInfo entity. Returns the entity or null if one does not exist.
    private Entity getUserInfoEntity() {
        UserService userService = UserServiceFactory.getUserService();
        String id = userService.getCurrentUser().getUserId();
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Filter queryFilter = new FilterPredicate("id", Query.FilterOperator.EQUAL, id);
        Query query = new Query("UserInfo").setFilter(queryFilter);
        PreparedQuery results = datastore.prepare(query); 
        Entity userInfoEntity = results.asSingleEntity(); 

        return userInfoEntity;
    }

    // Changes the value of the maximum number of comment per page property in AllComments and updates the datastore
    private void changeUserInfoMax(long newMax) {
        Entity userInfoEntity = getUserInfoEntity();

        userInfoEntity.setProperty("max", newMax);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(userInfoEntity);
    }
}
