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

function getComments() {
    // Get the username of the user who is currently logged in. Null if the user is not logged in.
    var username = null;
    fetch("/login-status").then(response => response.json()).then((loginInfo) => {
        username = loginInfo.username;
    });

    // Display the comments
    fetch('/data').then(response => response.json()).then((comments) => {
        comments.forEach((comment) => {
            document.getElementById('comments-list').appendChild(createCommentBox(comment, username));
        })
    });
}

function createCommentBox(comment, username) {
    const commmentElement = document.createElement('li');
    commmentElement.className = 'list-group-item';

    const contentElement = document.createElement('p');
    contentElement.className = 'mb-1';
    contentElement.style.textAlign = 'left';
    contentElement.innerHTML = comment.content;

    const timeElement = document.createElement('small');
    timeElement.innerHTML = new Date(comment.time);

    const nameElement = document.createElement('h3');
    nameElement.style.textAlign = 'left';
    nameElement.style.display = 'inline';
    nameElement.innerHTML = comment.name;

    const usernameElement = document.createElement('h3');
    usernameElement.style.textAlign = 'left';
    usernameElement.style.display = 'inline';
    usernameElement.style.padding = '30px';
    usernameElement.innerHTML = "(@" + comment.username + ")";

    const topLineElement = document.createElement('div');
    topLineElement.className = 'd-flex w-100 justify-content-between';
    topLineElement.style.textAlign = 'right';
    topLineElement.appendChild(timeElement);

    const nameLineElement = document.createElement('div');
    nameLineElement.className = 'd-flex w-100 justify-content-between';
    nameLineElement.style.textAlign = 'left';
    nameLineElement.appendChild(nameElement);
    nameLineElement.appendChild(usernameElement);

    const upIconElement = document.createElement('span');
    upIconElement.className = 'glyphicon glyphicon-thumbs-up';
    upIconElement.addEventListener('click', () => {
        upComment(comment);

        // Reload page to update it
        location.reload();
    });

    const upElement = document.createElement('p');
    upElement.innerHTML = comment.thumbsup;
    upElement.style.display = 'inline';
    upElement.style.padding = '20px';
    upElement.appendChild(upIconElement);

    const downIconElement = document.createElement('span');
    downIconElement.className = 'glyphicon glyphicon-thumbs-down';
    downIconElement.addEventListener('click', () => {
        downComment(comment);

        // Reload page to update it
        location.reload();
    });

    const downElement = document.createElement('p');
    downElement.innerHTML = comment.thumbsdown;
    downElement.style.display = 'inline';
    downElement.appendChild(downIconElement);

    const reactionsLineElement = document.createElement('div');
    reactionsLineElement.className = 'd-flex w-100 justify-content-between';
    reactionsLineElement.style.textAlign = 'right';
    reactionsLineElement.appendChild(upElement);
    reactionsLineElement.appendChild(downElement);

    const deleteButtonElement = document.createElement('button');
    deleteButtonElement.innerText = 'Delete';
    deleteButtonElement.style.textAlign = 'right';
    deleteButtonElement.className = 'btn btn-default btn-lg';
    deleteButtonElement.addEventListener('click', () => {
        deleteComment(comment);

        commmentElement.remove();
        
        // Reload page to update it
        location.reload();
    });

    // Hide delete box if the comment was not posted by the currently logged in user.
    if (comment.username != username) {
        deleteButtonElement.style.display = 'none';
    }

    const buttonLineElement = document.createElement('div');
    buttonLineElement.className = 'd-flex w-100 justify-content-between';
    buttonLineElement.appendChild(deleteButtonElement);

    commmentElement.appendChild(topLineElement);
    commmentElement.appendChild(nameLineElement);
    commmentElement.appendChild(contentElement);
    commmentElement.appendChild(reactionsLineElement);
    commmentElement.appendChild(buttonLineElement);
    return commmentElement;
}

function deleteComment(comment) {
  const params = new URLSearchParams();
  params.append('id', comment.id);
  fetch('/delete-data', {method: 'POST', body: params});
}

function upComment(comment) {
  const params = new URLSearchParams();
  params.append('id', comment.id);
  fetch('/thumbsup-data', {method: 'POST', body: params});
}

function downComment(comment) {
  const params = new URLSearchParams();
  params.append('id', comment.id);
  fetch('/thumbsdown-data', {method: 'POST', body: params});
}

function getPagination() {
    fetch('/pagination').then(response => response.json()).then((info) => {
        const buttons = Math.ceil(info.total / info.max);
        for (var i = 0; i < buttons; i++) {
            document.getElementById('pagination-list').appendChild(createPaginationBox(i));
        }
    });
}

function createPaginationBox(i) {
    const linkElement = document.createElement('a');
    linkElement.className = 'page-link';
    linkElement.innerHTML = i + 1;

    const itemElement = document.createElement('li');
    itemElement.className = 'page-item';
    itemElement.appendChild(linkElement);
    itemElement.addEventListener('click', () => {
        changePages(i);

        // Reload page to update it
        location.reload();
    });

    return itemElement;
}

function changePages(i) {
  const params = new URLSearchParams();
  params.append('i', i);
  fetch('/pagination', {method: 'POST', body: params});
}

function getSubmitForm() {
    fetch("/login-status").then(response => response.json()).then((loginInfo) => {
        // Display submit comment form only if the user is logged in. Otherwise, display login form.
        if (loginInfo.status === "True") {
            if (loginInfo.username === "null") {
                window.location.replace("/username.html");
                return;
            }
            const submitForm = document.getElementById("submit-comment-form");
            submitForm.style.display = 'block';
            const logoutHREF = document.getElementById("logout-href");
            logoutHREF.href = loginInfo.logoutUrl;
        } else {
            // Display login form.
            const loginForm = document.getElementById("login-form");
            loginForm.style.display = 'block';
            const loginHREF = document.getElementById("login-href");
            loginHREF.href = loginInfo.loginUrl;
        }  
    });
}
