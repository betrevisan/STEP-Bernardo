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

/** A comment on my portfolio */
public final class Comment {

  private final long id;
  private final String content;
  private final long time;
  private long thumbsup;
  private long thumbsdown;
  private long popularity;
  private final String name;
  private final String email;
  private final String username;

  public Comment(long id, String content, long time, long thumbsup, long thumbsdown, String name, String email, String username) {
    this.id = id;
    this.content = content;
    this.time = time;
    this.thumbsup = thumbsup;
    this.thumbsdown = thumbsdown;
    this.popularity = thumbsup - thumbsdown;
    this.name = name;
    this.email = email;
    this.username = username;
  }
}
