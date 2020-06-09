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
