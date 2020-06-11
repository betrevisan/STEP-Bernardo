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

function getLoginLogout() {
    fetch("/login-status").then(response => response.json()).then((loginInfo) => {
        // Display Log Out if the user is logged in. Otherwise, display Log In.
        if (loginInfo.status === "True") {
            const loginNav = document.getElementById("login-logout");
            loginNav.innerHTML = "LOG OUT";
            loginNav.href = loginInfo.logoutUrl;
        } else {
            const loginNav = document.getElementById("login-logout");
            loginNav.innerHTML = "LOG IN";
            loginNav.href = loginInfo.loginUrl;
        }  
    });
}

function changeWhere(newLocation) {
    const params = new URLSearchParams();
    params.append('newLocation', newLocation);
    fetch('/where', {method: 'POST', body: params});
}
