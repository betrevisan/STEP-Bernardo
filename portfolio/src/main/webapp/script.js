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
    fetch('/data').then(response => response.text()).then((comments) => {
        document.getElementById('comments-heading').innerHTML = comments;
        console.log(comments);
    });

}
