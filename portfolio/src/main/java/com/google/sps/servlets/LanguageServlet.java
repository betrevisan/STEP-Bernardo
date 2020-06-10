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

@WebServlet("/translate")
public final class LanguageServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get the language input from the form.
        String language = getParameter(request, "translate-comments", null);

        Entity allCommentsEntity = getAllCommentsEntity();

        // Update the language property
        allCommentsEntity.setProperty("language", language);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        // Add the updated entity back in the datastore
        datastore.put(allCommentsEntity);

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
}
