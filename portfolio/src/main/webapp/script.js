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

/**
 * Adds a random Arctic Monkeys song to the page.
 */
function addRandomAMSong() {
  const songs =
      [['Mardy Bum', 'https://www.youtube.com/embed/dO368WjwyFs?autoplay=1'], ['A Certain Romance', 'https://www.youtube.com/embed/zMupng6KQeE?autoplay=1'],
      ['I Wanna Be Yours', 'https://www.youtube.com/embed/fJLQCf4mFP0?autoplay=1'], ['No 1 Party Anthem ', 'https://www.youtube.com/embed/pDYlWAf-ekk?autoplay=1'],
      ['Snap Out of It', 'https://www.youtube.com/embed/1_O_T6Aq85E?autoplay=1'], ['Arabella', 'https://www.youtube.com/embed/Nj8r3qmOoZ8?autoplay=1'],
      ['Teddy Picker', 'https://www.youtube.com/embed/2A2XBoxtcUA?autoplay=1'], ['505', 'https://www.youtube.com/embed/qU9mHegkTc4?autoplay=1'],
      ['I Bet You Look Good On The Dancefloor', 'https://www.youtube.com/embed/pK7egZaT3hs?autoplay=1'],
      ['R U Mine?', 'https://www.youtube.com/embed/VQH8ZTgna3Q?autoplay=1'], ['Old Yellow Bricks', 'https://www.youtube.com/embed/xLaeOrDmWQ4?autoplay=1'],
      ['Fluorescent Adolescent', 'https://www.youtube.com/embed/ma9I9VBKPiw?autoplay=1'], ['Riot Van', 'https://www.youtube.com/embed/2XSOI72rZlw?autoplay=1'],
      ['Do I Wanna Know', 'https://www.youtube.com/embed/bpOSxM0rNPM?autoplay=1'], ['When The Sun Goes Down', 'https://www.youtube.com/embed/EqkBRVukQmE?autoplay=1']];

  // Pick a random AM song.
  const song = songs[Math.floor(Math.random() * songs.length)];

  // Add it to the page.
  const songContainer = document.getElementById('song-container');
  songContainer.innerText = song[0];

  // Add video to the page.
  const songVideo = document.getElementById('song-video');
  songVideo.style.display = 'inline-block';
  songVideo.src = song[1];
}

function getComments() {
    fetch('/data').then(response => response.json()).then((comments) => {
        comments.forEach((comment) => {
            document.getElementById('comments-list').appendChild(createCommentBox(comment));
        })
    });
}

function createCommentBox(comment) {
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
    nameElement.innerHTML = comment.name;

    const topLineElement = document.createElement('div');
    topLineElement.className = 'd-flex w-100 justify-content-between';
    topLineElement.style.textAlign = 'right';
    topLineElement.appendChild(timeElement);
    topLineElement.appendChild(nameElement);

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
    reactionsLineElement.style.textAlign = 'right';
    deleteButtonElement.className = 'btn btn-default btn-lg';
    deleteButtonElement.addEventListener('click', () => {
        deleteComment(comment);

        commmentElement.remove();
        
        // Reload page to update it
        location.reload();
    });

    
    const buttonLineElement = document.createElement('div');
    buttonLineElement.className = 'd-flex w-100 justify-content-between';
    buttonLineElement.appendChild(deleteButtonElement);

    commmentElement.appendChild(topLineElement);
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
        info.forEach((entity) => {
            const buttons = Math.ceil(entity.total / entity.max);
            for (var i = 0; i < buttons; i++) {
                document.getElementById('pagination-list').appendChild(createPaginationBox(i));
            }
        })
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
