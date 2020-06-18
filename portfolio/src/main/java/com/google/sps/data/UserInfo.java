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

package com.google.sps.data;

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

/** User Info for each registered user */
public final class UserInfo {

    private long id;
    private long max;
    private long page;
    private final String name;
    private final String username;
    private String filter;
    private String searchBy;
    private List<Key> liked;
    private List<Key> unliked;

    public UserInfo(Entity userInfoEntity) {
        this.id = (long) userInfoEntity.getKey().getId();
        this.max = (long) userInfoEntity.getProperty("max");
        this.page = (long) userInfoEntity.getProperty("page");
        this.name = (String) userInfoEntity.getProperty("name");
        this.username = (String) userInfoEntity.getProperty("username");
        this.filter = (String) userInfoEntity.getProperty("filter");
        this.searchBy = (String) userInfoEntity.getProperty("searchBy");
        this.liked = (List<Key>) userInfoEntity.getProperty("liked");
        this.unliked = (List<Key>) userInfoEntity.getProperty("unliked");
    }

    public void addToLikedComments(Entity commentEntity) {
        if (this.liked == null) {
            this.liked =  new ArrayList<Key>();
        }

        this.liked.add(commentEntity.getKey());
        return;
    }

    public void removeFromLikedComments(Entity commentEntity) {
        this.liked.remove(commentEntity.getKey());
        return;
    }

    public boolean isLikedComment(Entity commentEntity) {
        Key commentKey = commentEntity.getKey();

        return liked != null && liked.contains(commentKey);
    }

    public void addToUnlikedComments(Entity commentEntity) {
        if (this.unliked == null) {
            this.unliked =  new ArrayList<Key>();
        }

        this.unliked.add(commentEntity.getKey());
        return;
    }

    public void removeFromUnlikedComments(Entity commentEntity) {
        this.unliked.remove(commentEntity.getKey());
        return;
    }

    public boolean isUnlikedComment(Entity commentEntity) {
        Key commentKey = commentEntity.getKey();

        return unliked != null && unliked.contains(commentKey);
    }

    public void updateDatabase(Entity userInfoEntity, Transaction txn) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        // Update properties that can be changed.
        userInfoEntity.setProperty("unliked", this.unliked);
        userInfoEntity.setProperty("liked", this.liked);

        // Add the updated entity back in the datastore
        datastore.put(txn, userInfoEntity);
        
        return;
    }
}
