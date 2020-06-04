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

@WebServlet("/pagination")
public final class PaginationServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query query = new Query("AllComments");
        PreparedQuery results = datastore.prepare(query);
        Iterator<Entity> iter = results.asIterator();
        Entity entity = iter.next();

        List<AllComments> info = new ArrayList<>();
        long total = (long) entity.getProperty("total");
        long max = (long) entity.getProperty("max");
        long page = (long) entity.getProperty("page");
        String filter = (String) entity.getProperty("filter");
        info.add(new AllComments(total, max, page, filter));
        
        // Convert to json
        String json = convertToJsonUsingGson(info);
        response.setContentType("application/json;");
        response.getWriter().println(json);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query query = new Query("AllComments");
        PreparedQuery results = datastore.prepare(query);
        Iterator<Entity> iter = results.asIterator();
        Entity entity = iter.next();

        long newPage = Long.parseLong(request.getParameter("i")) + 1;
        entity.setProperty("page", newPage);
        datastore.put(entity);

        response.sendRedirect("/contact.html");
        return;
    }

    /**
    * Converts the comments array  into a JSON string using the Gson library. Note: We first added
    * the Gson library dependency to pom.xml.
    */
    private String convertToJsonUsingGson(List<AllComments> allComments) {
        Gson gson = new Gson();
        String json = gson.toJson(allComments);
        return json;
    }
}
