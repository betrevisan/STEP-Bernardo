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
import java.util.Iterator;



@WebServlet("/delete-data")
public final class DeleteServlet extends HttpServlet {

    private Key allKey;

    @Override
    public void init() {
        // Query the AllComments entity
        Query query = new Query("AllComments");
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);

        Iterator<Entity> iter = results.asIterator();
        Entity entity = iter.next();
        allKey = entity.getKey();
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long id = Long.parseLong(request.getParameter("id"));

        Key commentEntityKey = KeyFactory.createKey("Comment", id);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.delete(commentEntityKey);

        changeAllCommentsTotal(-1);

        response.sendRedirect("/contact.html");
        return;
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
