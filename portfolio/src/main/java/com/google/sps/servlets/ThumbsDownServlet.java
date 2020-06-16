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
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;

@WebServlet("/thumbsdown-data")
public final class ThumbsDownServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        if (!userService.isUserLoggedIn()) {
            response.sendRedirect("/contact.html");
            return;
        }

        Entity userInfoEntity = getUserInfoEntity();
        
        // Get comment's id (which was passed as a parameter).
        long id = Long.parseLong(request.getParameter("id"));

        Entity commentEntity = getCommentEntity(id);
        if (commentEntity == null) {
            response.setContentType("text/html;");
            response.getWriter().println("Unable to get comment.");
            return;
        }

        Comment comment = new Comment(commentEntity);

        if (isUnlikedComment(userInfoEntity, commentEntity)) {
            comment.decrementThumbsdown();
            comment.incrementPopularity();
            removeFromUnlikedComments(userInfoEntity, commentEntity);
        } else {
            comment.incrementThumbsdown();
            comment.decrementPopularity();
            addToUnlikedComments(userInfoEntity, commentEntity);
        }

        comment.updateDatabase(commentEntity);

        response.sendRedirect("/contact.html");
        return;
    }

    // Accesses the datastore to get the UserInfo entity. Returns the entity or null if one does not exist.
    private Entity getCommentEntity(long id) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        // Using the id, get the comment's key.
        Key commentEntityKey;
        try {
            commentEntityKey = KeyFactory.createKey("Comment", id);
        } catch(Exception e) {
            return null;
        }

        // Get the comment entity using its key.
        Entity commentEntity;
        try {
            commentEntity = datastore.get(commentEntityKey);
        } catch(Exception e) {
            return null;
        }

        return commentEntity;
    }
    
    // Accesses the datastore to get the UserInfo entity. Returns the entity or null if one does not exist.
    private Entity getUserInfoEntity() {
        UserService userService = UserServiceFactory.getUserService();
        String id = userService.getCurrentUser().getUserId();
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Filter queryFilter = new FilterPredicate("id", Query.FilterOperator.EQUAL, id);
        Query query = new Query("UserInfo").setFilter(queryFilter);

        return datastore.prepare(query).asSingleEntity();
    }

    private void addToUnlikedComments(Entity userInfoEntity, Entity commentEntity) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        List<Key> unliked = (ArrayList<Key>) userInfoEntity.getProperty("unliked");

        if (unliked == null) {
            unliked =  new ArrayList<Key>();
        }

        unliked.add(commentEntity.getKey());
        userInfoEntity.setProperty("unliked", unliked);
        datastore.put(userInfoEntity);
    }

    private void removeFromUnlikedComments(Entity userInfoEntity, Entity commentEntity) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        List<Key> unliked = (ArrayList<Key>) userInfoEntity.getProperty("unliked");

        unliked.remove(commentEntity.getKey());
        userInfoEntity.setProperty("unliked", unliked);
        datastore.put(userInfoEntity);
    }

    private boolean isUnlikedComment(Entity userInfoEntity, Entity commentEntity) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        List<Key> unliked = (ArrayList<Key>) userInfoEntity.getProperty("unliked");
        Key commentKey = commentEntity.getKey();

        return unliked != null && unliked.contains(commentKey);
    }
}
