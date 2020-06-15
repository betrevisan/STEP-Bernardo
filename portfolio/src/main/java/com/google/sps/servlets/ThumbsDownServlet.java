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
import java.util.Optional;
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
import com.google.appengine.api.datastore.EntityNotFoundException;

@WebServlet("/thumbsdown-data")
public final class ThumbsDownServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {        
        // Get comment's id (which was passed as a parameter).
        long id = Long.parseLong(Optional.ofNullable(request.getParameter("id")).orElse(null));

        // Using the id, get the comment's key.
        Key commentEntityKey;
        try {
            commentEntityKey = KeyFactory.createKey("Comment", id);
        } catch(NullPointerException e) {
            response.setContentType("text/html;");
            response.getWriter().println("Unable to get comment's key.");
            return;
        }

        // Instantiate datastore
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        // Get the comment entity using its key.
        Entity commentEntity;
        try {
            commentEntity = datastore.get(commentEntityKey);
        } catch(EntityNotFoundException e) {
            response.setContentType("text/html;");
            response.getWriter().println("Unable to get comment.");
            return;
        }

        // Get the previous thumbs down value.
        long prevThumbsDown = (long) commentEntity.getProperty("thumbsdown");
        // Get the previous popularity value.
        long prevPopularity = (long) commentEntity.getProperty("popularity");
        long newThumbsDown = prevThumbsDown + 1;
        long newPopularity = prevPopularity - 1;
        
        // Update the thumbs down property to be the previous value plus one.
        commentEntity.setProperty("thumbsdown", newThumbsDown);
        // Update the popularity property to be the previous one minus one.
        commentEntity.setProperty("popularity", newPopularity);

        // Add the updated entity back in the datastore
        datastore.put(commentEntity);

        response.sendRedirect("/contact.html");
        return;
    }
}
