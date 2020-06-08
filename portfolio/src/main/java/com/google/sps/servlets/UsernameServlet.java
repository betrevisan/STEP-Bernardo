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

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/username")
public class UsernameServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        if (!userService.isUserLoggedIn()) {
            response.sendRedirect("/contact.html");
            return;
        }

        String username = request.getParameter("user-username");
        // Ask for username again if the username chosen was not available.
        if (usernameCheck(username).equals(false)) {
            response.sendRedirect("/contact.html");
            return;
        }
        String id = userService.getCurrentUser().getUserId();

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity entity = new Entity("UserInfo", id);
        entity.setProperty("id", id);
        entity.setProperty("username", username);
        datastore.put(entity);

        response.sendRedirect("/contact.html");
    }

    // Returns the true if the username is available to be used, otherwise returns false.
    private Boolean usernameCheck(String username) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Filter queryFilter = new FilterPredicate("username", Query.FilterOperator.EQUAL, username);
        Query query = new Query("UserInfo").setFilter(queryFilter);
        PreparedQuery results = datastore.prepare(query); 
        Entity entity = results.asSingleEntity(); 

        if (entity == null) {
            return true;
        } else {
            return false;
        }    
    }
}