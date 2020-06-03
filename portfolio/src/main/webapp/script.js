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
      ['Mardy Bum', 'A Certain Romance', 'I Wanna Be Yours', 'No. 1 Party Anthem ',
      'Snap Out of It', 'Arabella', 'Teddy Picker', '505', 'I Bet You Look Good On The Dancefloor',
      'Are U Mine', 'Old Yellow Bricks', 'Fluorescent Adolescent', 'Riot Van', 'Do I Wanna Know',
      'When The Sun Goes Down'];

  // Pick a random AM song.
  const song = songs[Math.floor(Math.random() * songs.length)];

  // Add it to the page.
  const songContainer = document.getElementById('song-container');
  songContainer.innerText = song;
}

function getComments() {
    fetch('/data').then(response => response.json()).then((comments) => {
        console.log(comments);
        comments.forEach((comment) => {
            document.getElementById('comments-list').appendChild(createCommentBox(comment));
        })
        console.log(comments);
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

    const topLineElement = document.createElement('div');
    topLineElement.className = 'd-flex w-100 justify-content-between';
    topLineElement.style.textAlign = 'right';
    topLineElement.appendChild(timeElement);

    const upElement = document.createElement('small');
    upElement.innerHTML = comment.thumbsup;

    const downElement = document.createElement('small');
    downElement.innerHTML = comment.thumbsdown;

    const deleteButtonElement = document.createElement('button');
    deleteButtonElement.innerText = 'Delete';
    deleteButtonElement.className = 'btn btn-default btn-lg';
    deleteButtonElement.addEventListener('click', () => {
        deleteComment(comment);

        commmentElement.remove();
    });

    const upButtonElement = document.createElement('button');
    upButtonElement.innerText = 'Thumbs Up';
    upButtonElement.className = 'btn btn-default btn-lg';
    upButtonElement.addEventListener('click', () => {
        upComment(comment);
    });

    const downButtonElement = document.createElement('button');
    downButtonElement.innerText = 'Thumbs Down';
    downButtonElement.className = 'btn btn-default btn-lg';
    downButtonElement.addEventListener('click', () => {
        downComment(comment);
    });

    const buttonLineElement = document.createElement('div');
    buttonLineElement.className = 'd-flex w-100 justify-content-between';
    buttonLineElement.appendChild(upButtonElement);
    buttonLineElement.appendChild(downButtonElement);
    buttonLineElement.appendChild(deleteButtonElement);

    commmentElement.appendChild(topLineElement);
    commmentElement.appendChild(contentElement);
    commmentElement.appendChild(upElement);
    commmentElement.appendChild(downElement);
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
