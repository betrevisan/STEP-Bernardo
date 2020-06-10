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

//Referenced to https://developers.google.com/maps/documentation/javascript/examples

function getLoginLogout() {
    fetch("/login-status").then(response => response.json()).then((loginInfo) => {
        if (loginInfo.status === "True") {
            const loginLogout = document.getElementById("login-logout");
            loginLogout.innerHTML = "LOG OUT";
            loginLogout.href = loginInfo.logoutUrl;
        } else {
            const loginLogout = document.getElementById("login-logout");
            loginLogout.innerHTML = "LOG IN";
            loginLogout.href = loginInfo.loginUrl;
        }  
    });
}
