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

/** All comments on my portfolio */
public final class AllComments {

  private long total;
  private long max;
  private long page;
  private String filter;

  public AllComments(long total, long max, long page, String filter) {
    this.total = total;
    this.max = max;
    this.page = page;
    this.filter = filter;
  }
}