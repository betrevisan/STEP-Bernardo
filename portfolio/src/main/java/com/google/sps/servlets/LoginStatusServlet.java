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

@WebServlet("/login-status")
public class LoginStatusServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;");

        UserService userService = UserServiceFactory.getUserService();

        if (userService.isUserLoggedIn()) {
            Entity userInfoEntity = getUserInfoEntity();

            String where = "/contact.html";
            try {
                where = (String) userInfoEntity.getProperty("where");
            } catch (NullPointerExcegption e) {
                // If there is nothing in the where filed, set it to contact.html by default.
            }

            String logoutUrl;
            try {
                logoutUrl = userService.createLogoutURL(where);
            } catch (NullPointerException e) {
                logoutUrl = userService.createLogoutURL("/contact.html");
            }

            String username = getUsername(userService.getCurrentUser().getUserId());

            if (username == null) {
                response.getWriter().println("{\"status\": \"True\", \"logoutUrl\": \"" + logoutUrl + "\", \"username\": \"null\"}");
            } else {
                response.getWriter().println("{\"status\": \"True\", \"logoutUrl\": \"" + logoutUrl + "\", \"username\": \"" + username + "\"}");
            }

        } else {
            String loginUrl = userService.createLoginURL("/contact.html");
            response.getWriter().println("{\"status\": \"False\", \"loginUrl\": \"" + loginUrl + "\"}");
        }
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
        UserService userService = UserServiceFactory.getUserService();
        String id = userService.getCurrentUser().getUserId();
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Filter queryFilter = new FilterPredicate("id", Query.FilterOperator.EQUAL, id);
        Query query = new Query("UserInfo").setFilter(queryFilter);
        PreparedQuery results = datastore.prepare(query); 
        Entity userInfoEntity = results.asSingleEntity(); 

        return userInfoEntity;
    }
}
