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
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

/** A comment on my portfolio */
public final class Comment {

    private final long id;
    private String content;
    private final long time;
    private long thumbsup;
    private long thumbsdown;
    private long popularity;
    private final String name;
    private final String email;
    private final String username;

    public Comment(Entity commentEntity) {
        this.id = (long) commentEntity.getKey().getId();
        this.content = (String) commentEntity.getProperty("content");
        this.time = (long) commentEntity.getProperty("time");
        this.thumbsup = (long) commentEntity.getProperty("thumbsup");
        this.thumbsdown = (long) commentEntity.getProperty("thumbsdown");
        this.popularity = (long) commentEntity.getProperty("popularity");
        this.name = (String) commentEntity.getProperty("name");
        this.email = (String) commentEntity.getProperty("email");
        this.username = (String) commentEntity.getProperty("username");
    }

     public void incrementThumbsup() {
        this.thumbsup++;
        return;
    }

    public void decrementThumbsup() {
        this.thumbsup--;
        return;
    }

    public void incrementThumbsdown() {
        this.thumbsdown++;
        return;
    }

    public void decrementThumbsdown() {
        this.thumbsdown--;
        return;
    }

    public void incrementPopularity() {
        this.popularity++;
        return;
    }

    public void decrementPopularity() {
        this.popularity--;
        return;
    }

    public void translateComment(String language) {
        Translate translate = TranslateOptions.getDefaultInstance().getService();

        try {
            // Translate comment
            Translation translation = translate.translate(this.content, Translate.TranslateOption.targetLanguage(language));
            this.content = translation.getTranslatedText();
            return;
        } catch (RuntimeException e) {
            return;
        }
    }

    public void updateDatabase(Entity commentEntity, Transaction txn) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        // Update properties that can be changed.
        commentEntity.setProperty("thumbsup", this.thumbsup);
        commentEntity.setProperty("thumbsdown", this.thumbsdown);
        commentEntity.setProperty("popularity", this.popularity);

        // Add the updated entity back in the datastore
        datastore.put(txn, commentEntity);

        return;
    }
}
