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
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/where")
public class WhereServlet extends HttpServlet {

     @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        // If the user is not logged in, then there is nothing to update.
        if (!userService.isUserLoggedIn()) {
            return;
        }

        Transaction txn = datastore.beginTransaction();
        try {
            String newLocation = request.getParameter("newLocation");

            Entity userInfoEntity = getUserInfoEntity();

            // Update the where property
            userInfoEntity.setProperty("where", newLocation);

            // Add the updated entity back in the datastore
            datastore.put(userInfoEntity);
            txn.commit();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }

        return;
    }

    // Accesses the datastore to get the UserInfo entity. Returns the entity or null if one does not exist.
    private Entity getUserInfoEntity() {
        UserService userService = UserServiceFactory.getUserService();
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Transaction txn = datastore.beginTransaction();
        try {
            String id = userService.getCurrentUser().getUserId();
            Filter queryFilter = new FilterPredicate("id", Query.FilterOperator.EQUAL, id);
            Query query = new Query("UserInfo").setFilter(queryFilter);
            PreparedQuery results = datastore.prepare(query); 
            Entity userInfoEntity = results.asSingleEntity();
            txn.commit();
            
            return userInfoEntity;
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }
}